# infrastructure/variables.tf

variable "resource_group_name" {
  description = "Name of the existing resource group"
  type        = string
  default     = "rg-lhc-tests"
}

variable "project_name" {
  description = "Name of the project"
  type        = string
  default     = "java-simple"
}

variable "environment_name" {
  description = "Environment name"
  type        = string
  default     = "dev"
}

variable "acr_name" {
  description = "Azure Container Registry name"
  type        = string
  default     = "javademoapp"
}

variable "acr_sku" {
  description = "ACR SKU"
  type        = string
  default     = "Basic"
}

variable "container_environment_name" {
  description = "Container Apps Environment name"
  type        = string
  default     = "java-app-env"
}

variable "container_app_name" {
  description = "Container App name"
  type        = string
  default     = "java-app"
}

variable "image_repository" {
  description = "Container image repository name"
  type        = string
  default     = "java-simple-app"
}

variable "placeholder_image" {
  description = "Placeholder image used during initial Container App creation"
  type        = string
  default     = "mcr.microsoft.com/azuredocs/containerapps-helloworld:latest"  # Microsoft's hello world image
  # Alternative options:
  # default     = "nginx:alpine"  # Simple nginx
  # default     = "httpd:alpine"  # Apache httpd
}

variable "container_cpu" {
  description = "Container CPU allocation"
  type        = number
  default     = 0.25
}

variable "container_memory" {
  description = "Container memory allocation"
  type        = string
  default     = "0.5Gi"
}

variable "min_replicas" {
  description = "Minimum number of replicas"
  type        = number
  default     = 0
}

variable "max_replicas" {
  description = "Maximum number of replicas"
  type        = number
  default     = 3
}

# Local values
locals {
  common_tags = {
    Environment = var.environment_name
    Project     = var.project_name
    ManagedBy   = "Terraform"
  }
}