package il.cshaifa.hmo_system.client_base.base_controllers;

import il.cshaifa.hmo_system.entities.Role;

public abstract class RoleDefinedViewController extends ViewController {
  protected final Role role;

  public RoleDefinedViewController(Role role) {
    this.role = role;
  }

  protected abstract void applyRoleBehavior();
}
