package com.world.config;

import java.util.Comparator;

class ComparatorConfig
  implements Comparator
{
  public int compare(Object arg0, Object arg1)
  {
    Config user0 = (Config)arg0;
    Config user1 = (Config)arg1;

    if (user0.startNumber() > user1.startNumber())
      return 1;
    if (user0.startNumber() == user1.startNumber()) {
      return 0;
    }
    return -1;
  }
}