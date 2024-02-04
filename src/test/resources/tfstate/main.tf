terraform {
  backend "http" {
    address        = "http://localhost:8081/api/v1/tfstate/remote"
    lock_address   = "http://localhost:8081/api/v1/tfstate/remote/lock"
    unlock_address = "http://localhost:8081/api/v1/tfstate/remote/lock"
    lock_method    = "POST"
    unlock_method  = "DELETE"
    retry_wait_max = 2
    retry_max      = 1
    retry_wait_min = 1
    username       = "root1"
    password       = "root1"
  }
  required_providers {
    null = {
      source  = "hashicorp/null"
      version = "3.2.2"
    }
  }
}

resource "null_resource" "cluster" {
  # Changes to any instance of the cluster requires re-provisioning
}
