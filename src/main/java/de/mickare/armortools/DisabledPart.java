package de.mickare.armortools;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public @RequiredArgsConstructor enum DisabledPart {
  ALL(~0), HAND(0, 0), BOOTS(1, 0), LEGS(2, 0), CHEST(3, 0), HELMET(4, 0);

  // Bits
  // 0     Disable removing all part, and placing/replacing hand
  // 1 - 4 Disable Removing
  // 8 - 9 Disable Replacing/Removing
  // 16-20 Disable Placing
  private DisabledPart(int part, int isPart) {
    this(1 << (part) | 1 << (part + 8) | 1 << (part + 16));
  }

  @Getter
  private final int mask;

  public int apply(int old, boolean on) {
    if (on) {
      return old | mask;
    } else {
      return old & (~mask);
    }
  }

}
