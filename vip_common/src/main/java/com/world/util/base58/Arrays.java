package com.world.util.base58;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

public final class Arrays {
    private Arrays() {
    }

    public static boolean areEqual(boolean[] a, boolean[] b) {
        if(a == b) {
            return true;
        } else if(a != null && b != null) {
            if(a.length != b.length) {
                return false;
            } else {
                for(int i = 0; i != a.length; ++i) {
                    if(a[i] != b[i]) {
                        return false;
                    }
                }

                return true;
            }
        } else {
            return false;
        }
    }

    public static boolean areEqual(char[] a, char[] b) {
        if(a == b) {
            return true;
        } else if(a != null && b != null) {
            if(a.length != b.length) {
                return false;
            } else {
                for(int i = 0; i != a.length; ++i) {
                    if(a[i] != b[i]) {
                        return false;
                    }
                }

                return true;
            }
        } else {
            return false;
        }
    }

    public static boolean areEqual(byte[] a, byte[] b) {
        if(a == b) {
            return true;
        } else if(a != null && b != null) {
            if(a.length != b.length) {
                return false;
            } else {
                for(int i = 0; i != a.length; ++i) {
                    if(a[i] != b[i]) {
                        return false;
                    }
                }

                return true;
            }
        } else {
            return false;
        }
    }

    public static boolean constantTimeAreEqual(byte[] a, byte[] b) {
        if(a == b) {
            return true;
        } else if(a != null && b != null) {
            if(a.length != b.length) {
                return false;
            } else {
                int nonEqual = 0;

                for(int i = 0; i != a.length; ++i) {
                    nonEqual |= a[i] ^ b[i];
                }

                return nonEqual == 0;
            }
        } else {
            return false;
        }
    }

    public static boolean areEqual(int[] a, int[] b) {
        if(a == b) {
            return true;
        } else if(a != null && b != null) {
            if(a.length != b.length) {
                return false;
            } else {
                for(int i = 0; i != a.length; ++i) {
                    if(a[i] != b[i]) {
                        return false;
                    }
                }

                return true;
            }
        } else {
            return false;
        }
    }

    public static void fill(byte[] array, byte value) {
        for(int i = 0; i < array.length; ++i) {
            array[i] = value;
        }

    }

    public static void fill(long[] array, long value) {
        for(int i = 0; i < array.length; ++i) {
            array[i] = value;
        }

    }

    public static void fill(short[] array, short value) {
        for(int i = 0; i < array.length; ++i) {
            array[i] = value;
        }

    }

    public static int hashCode(byte[] data) {
        if(data == null) {
            return 0;
        } else {
            int i = data.length;
            int hc = i + 1;

            while(true) {
                --i;
                if(i < 0) {
                    return hc;
                }

                hc *= 257;
                hc ^= data[i];
            }
        }
    }

    public static byte[] clone(byte[] data) {
        if(data == null) {
            return null;
        } else {
            byte[] copy = new byte[data.length];
            System.arraycopy(data, 0, copy, 0, data.length);
            return copy;
        }
    }

    public static int[] clone(int[] data) {
        if(data == null) {
            return null;
        } else {
            int[] copy = new int[data.length];
            System.arraycopy(data, 0, copy, 0, data.length);
            return copy;
        }
    }
}

