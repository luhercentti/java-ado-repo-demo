# infrastructure/variables.tf
variable "project_name" {
  description = "Name of the project"
  type        = string
  default     = "java-demo-app-lhc"
}

variable "resource_group_name" {
  description = "Name of the existing resource group to use"
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
  description = "Name for the container app"
  type        = string
  default     = "java-app"
}

variable "container_environment_name" {
  description = "Name for container app environment"
  type        = string
  default     = "java-app-env"
}

variable "environment_name" {
  description = "Environment name for the application"
  type        = string
  default     = "development"
}

# Container resource settings
variable "container_cpu" {
  description = "CPU allocation for container"
  type        = number
  default     = 0.25
}

variable "container_memory" {
  description = "Memory allocation for container"
  type        = string
  default     = "0.5Gi"
}

variable "min_replicas" {
  description = "Minimum replicas"
  type        = number
  default     = 1
}

variable "max_replicas" {
  description = "Maximum replicas"
  type        = number
  default     = 3
}