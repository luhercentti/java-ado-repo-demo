# infrastructure/variables.tf
variable "project_name" {
  description = "Name of the project"
  type        = string
  default     = "java-demo-app"
}

variable "location" {
  description = "Azure region for all resources"
  type        = string
  default     = "East US"
}

variable "resource_group_name" {
  description = "Name of the main resource group"
  type        = string
  default     = "rg-lhc-tests"
}

variable "acr_name" {
  description = "Name of the Azure Container Registry"
  type        = string
  default     = "javademoapp"
}

variable "acr_sku" {
  description = "SKU for the Azure Container Registry"
  type        = string
  default     = "Basic"
  validation {
    condition     = contains(["Basic", "Standard", "Premium"], var.acr_sku)
    error_message = "ACR SKU must be Basic, Standard, or Premium."
  }
}

variable "image_repository" {
  description = "Name of the container image repository"
  type        = string
  default     = "java-simple-app"
}

variable "container_app_name" {
  description = "Base name for container apps"
  type        = string
  default     = "java-app"
}

variable "container_environment_name" {
  description = "Base name for container app environments"
  type        = string
  default     = "java-app-env"
}

# Environment flags
variable "create_dev_environment" {
  description = "Whether to create development environment"
  type        = bool
  default     = true
}

variable "create_prod_environment" {
  description = "Whether to create production environment"
  type        = bool
  default     = true
}

# Development environment settings
variable "dev_container_cpu" {
  description = "CPU allocation for development container"
  type        = number
  default     = 0.25
}

variable "dev_container_memory" {
  description = "Memory allocation for development container"
  type        = string
  default     = "0.5Gi"
}

variable "dev_min_replicas" {
  description = "Minimum replicas for development environment"
  type        = number
  default     = 1
}

variable "dev_max_replicas" {
  description = "Maximum replicas for development environment"
  type        = number
  default     = 3
}

# Production environment settings
variable "prod_container_cpu" {
  description = "CPU allocation for production container"
  type        = number
  default     = 0.5
}

variable "prod_container_memory" {
  description = "Memory allocation for production container"
  type        = string
  default     = "1.0Gi"
}

variable "prod_min_replicas" {
  description = "Minimum replicas for production environment"
  type        = number
  default     = 2
}

variable "prod_max_replicas" {
  description = "Maximum replicas for production environment"
  type        = number
  default     = 10
}

variable "environment" {
  description = "Current environment being deployed"
  type        = string
  default     = ""
  validation {
    condition     = contains(["", "development", "production"], var.environment)
    error_message = "Environment must be either 'development' or 'production' or empty for all."
  }
}