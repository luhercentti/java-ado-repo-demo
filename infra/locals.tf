# infrastructure/locals.tf
locals {
  common_tags = {
    Project     = var.project_name
    Environment = "shared"
    ManagedBy   = "Terraform"
    CreatedBy   = "AzureDevOps"
  }
}

# infrastructure/outputs.tf
output "resource_group_name" {
  description = "Name of the main resource group"
  value       = azurerm_resource_group.main.name
}

output "acr_login_server" {
  description = "Login server URL for the Azure Container Registry"
  value       = azurerm_container_registry.acr.login_server
}

output "acr_name" {
  description = "Name of the Azure Container Registry"
  value       = azurerm_container_registry.acr.name
}

output "acr_admin_username" {
  description = "Admin username for the Azure Container Registry"
  value       = azurerm_container_registry.acr.admin_username
  sensitive   = true
}

output "acr_admin_password" {
  description = "Admin password for the Azure Container Registry"
  value       = azurerm_container_registry.acr.admin_password
  sensitive   = true
}

output "dev_resource_group_name" {
  description = "Name of the development resource group"
  value       = var.create_dev_environment ? azurerm_resource_group.dev[0].name : null
}

output "prod_resource_group_name" {
  description = "Name of the production resource group"
  value       = var.create_prod_environment ? azurerm_resource_group.prod[0].name : null
}

output "dev_container_app_url" {
  description = "URL of the development container app"
  value       = var.create_dev_environment ? "https://${azurerm_container_app.dev[0].latest_revision_fqdn}" : null
}

output "prod_container_app_url" {
  description = "URL of the production container app"
  value       = var.create_prod_environment ? "https://${azurerm_container_app.prod[0].latest_revision_fqdn}" : null
}

output "dev_container_app_name" {
  description = "Name of the development container app"
  value       = var.create_dev_environment ? azurerm_container_app.dev[0].name : null
}

output "prod_container_app_name" {
  description = "Name of the production container app"
  value       = var.create_prod_environment ? azurerm_container_app.prod[0].name : null
}