# infrastructure/main.tf
terraform {
  required_version = ">= 1.0"
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 3.0"
    }
  }
  
  # Backend configuration - uncomment and configure for remote state
  # backend "azurerm" {
  #   resource_group_name  = "rg-terraform-state"
  #   storage_account_name = "terraformstateaccount"
  #   container_name       = "tfstate"
  #   key                  = "java-app.terraform.tfstate"
  # }
}

provider "azurerm" {
  features {
    resource_group {
      prevent_deletion_if_contains_resources = false
    }
  }
}

# Data source for current client configuration
data "azurerm_client_config" "current" {}

# Main Resource Group (for shared resources like ACR)
resource "azurerm_resource_group" "main" {
  name     = var.resource_group_name
  location = var.location

  tags = local.common_tags
}

# Development Resource Group
resource "azurerm_resource_group" "dev" {
  count    = var.create_dev_environment ? 1 : 0
  name     = "${var.resource_group_name}-dev"
  location = var.location

  tags = merge(local.common_tags, {
    Environment = "development"
  })
}

# Production Resource Group
resource "azurerm_resource_group" "prod" {
  count    = var.create_prod_environment ? 1 : 0
  name     = "${var.resource_group_name}-prod"
  location = var.location

  tags = merge(local.common_tags, {
    Environment = "production"
  })
}

# Azure Container Registry
resource "azurerm_container_registry" "acr" {
  name                = var.acr_name
  resource_group_name = azurerm_resource_group.main.name
  location           = azurerm_resource_group.main.location
  sku                = var.acr_sku
  admin_enabled      = true

  tags = local.common_tags
}

# Log Analytics Workspace for Container Apps
resource "azurerm_log_analytics_workspace" "main" {
  name                = "${var.project_name}-logs"
  location           = azurerm_resource_group.main.location
  resource_group_name = azurerm_resource_group.main.name
  sku                = "PerGB2018"
  retention_in_days  = 30

  tags = local.common_tags
}

# Development Container Apps Environment
resource "azurerm_container_app_environment" "dev" {
  count                      = var.create_dev_environment ? 1 : 0
  name                      = "${var.container_environment_name}-dev"
  location                  = azurerm_resource_group.dev[0].location
  resource_group_name       = azurerm_resource_group.dev[0].name
  log_analytics_workspace_id = azurerm_log_analytics_workspace.main.id

  tags = merge(local.common_tags, {
    Environment = "development"
  })
}

# Production Container Apps Environment
resource "azurerm_container_app_environment" "prod" {
  count                      = var.create_prod_environment ? 1 : 0
  name                      = "${var.container_environment_name}-prod"
  location                  = azurerm_resource_group.prod[0].location
  resource_group_name       = azurerm_resource_group.prod[0].name
  log_analytics_workspace_id = azurerm_log_analytics_workspace.main.id

  tags = merge(local.common_tags, {
    Environment = "production"
  })
}

# Development Container App
resource "azurerm_container_app" "dev" {
  count                        = var.create_dev_environment ? 1 : 0
  name                        = "${var.container_app_name}-dev"
  container_app_environment_id = azurerm_container_app_environment.dev[0].id
  resource_group_name         = azurerm_resource_group.dev[0].name
  revision_mode              = "Single"

  registry {
    server   = azurerm_container_registry.acr.login_server
    username = azurerm_container_registry.acr.admin_username
    password_secret_name = "acr-password"
  }

  secret {
    name  = "acr-password"
    value = azurerm_container_registry.acr.admin_password
  }

  template {
    container {
      name   = "java-app"
      image  = "${azurerm_container_registry.acr.login_server}/${var.image_repository}:latest"
      cpu    = var.dev_container_cpu
      memory = var.dev_container_memory

      env {
        name  = "ENVIRONMENT"
        value = "development"
      }
    }

    min_replicas = var.dev_min_replicas
    max_replicas = var.dev_max_replicas
  }

  ingress {
    allow_insecure_connections = false
    external_enabled          = true
    target_port              = 8080

    traffic_weight {
      percentage      = 100
      latest_revision = true
    }
  }

  tags = merge(local.common_tags, {
    Environment = "development"
  })
}

# Production Container App
resource "azurerm_container_app" "prod" {
  count                        = var.create_prod_environment ? 1 : 0
  name                        = "${var.container_app_name}-prod"
  container_app_environment_id = azurerm_container_app_environment.prod[0].id
  resource_group_name         = azurerm_resource_group.prod[0].name
  revision_mode              = "Single"

  registry {
    server   = azurerm_container_registry.acr.login_server
    username = azurerm_container_registry.acr.admin_username
    password_secret_name = "acr-password"
  }

  secret {
    name  = "acr-password"
    value = azurerm_container_registry.acr.admin_password
  }

  template {
    container {
      name   = "java-app"
      image  = "${azurerm_container_registry.acr.login_server}/${var.image_repository}:latest"
      cpu    = var.prod_container_cpu
      memory = var.prod_container_memory

      env {
        name  = "ENVIRONMENT"
        value = "production"
      }
    }

    min_replicas = var.prod_min_replicas
    max_replicas = var.prod_max_replicas
  }

  ingress {
    allow_insecure_connections = false
    external_enabled          = true
    target_port              = 8080

    traffic_weight {
      percentage      = 100
      latest_revision = true
    }
  }

  tags = merge(local.common_tags, {
    Environment = "production"
  })
}