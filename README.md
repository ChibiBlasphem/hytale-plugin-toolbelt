# Arcane Rig

> [!WARNING]
> This mod is heavily work in progress

Arcane Rig is an extensible equipment framework for Hytale mods, allowing developers to define new equippable slots with built-in persistence, validation, and window integration.

It also provides the GUI to equip and unequip items from it.

## Mod developers

### Installation steps (WIP)

### Registering a slot

In the `setup` method of your plugin:

```java
ArcaneRigPlugin.onApiReady(api -> {
  SlotDescriptor slot = SlotDescriptor.builder("ToolPickaxe")
      .displayName("Pickaxe")
      .canEquip(stack -> stack.getItemId().contains("Pickaxe"))
      .build();
  api.registerSlot(slot)
});
```
