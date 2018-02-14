package com.fasterxml.jackson.databind.util;

import java.io.Serializable;

public abstract class NameTransformer {
    public static final NameTransformer NOP = new NopTransformer();

    public static class Chained extends NameTransformer implements Serializable {
        private static final long serialVersionUID = 1;
        protected final NameTransformer _t1;
        protected final NameTransformer _t2;

        public Chained(NameTransformer t1, NameTransformer t2) {
            this._t1 = t1;
            this._t2 = t2;
        }

        public String transform(String name) {
            return this._t1.transform(this._t2.transform(name));
        }

        public String reverse(String transformed) {
            transformed = this._t1.reverse(transformed);
            if (transformed != null) {
                return this._t2.reverse(transformed);
            }
            return transformed;
        }

        public String toString() {
            return "[ChainedTransformer(" + this._t1 + ", " + this._t2 + ")]";
        }
    }

    protected static final class NopTransformer extends NameTransformer implements Serializable {
        private static final long serialVersionUID = 1;

        protected NopTransformer() {
        }

        public final String transform(String name) {
            return name;
        }

        public final String reverse(String transformed) {
            return transformed;
        }
    }

    public abstract String reverse(String str);

    public abstract String transform(String str);

    protected NameTransformer() {
    }

    public static NameTransformer simpleTransformer(final String prefix, final String suffix) {
        boolean hasPrefix;
        boolean hasSuffix = true;
        if (prefix == null || prefix.length() <= 0) {
            hasPrefix = false;
        } else {
            hasPrefix = true;
        }
        if (suffix == null || suffix.length() <= 0) {
            hasSuffix = false;
        }
        if (!hasPrefix) {
            return hasSuffix ? new NameTransformer() {
                public final String transform(String name) {
                    return name + suffix;
                }

                public final String reverse(String transformed) {
                    if (transformed.endsWith(suffix)) {
                        return transformed.substring(0, transformed.length() - suffix.length());
                    }
                    return null;
                }

                public final String toString() {
                    return "[SuffixTransformer('" + suffix + "')]";
                }
            } : NOP;
        } else {
            if (hasSuffix) {
                return new NameTransformer() {
                    public final String transform(String name) {
                        return prefix + name + suffix;
                    }

                    public final String reverse(String transformed) {
                        if (transformed.startsWith(prefix)) {
                            String str = transformed.substring(prefix.length());
                            if (str.endsWith(suffix)) {
                                return str.substring(0, str.length() - suffix.length());
                            }
                        }
                        return null;
                    }

                    public final String toString() {
                        return "[PreAndSuffixTransformer('" + prefix + "','" + suffix + "')]";
                    }
                };
            }
            return new NameTransformer() {
                public final String transform(String name) {
                    return prefix + name;
                }

                public final String reverse(String transformed) {
                    if (transformed.startsWith(prefix)) {
                        return transformed.substring(prefix.length());
                    }
                    return null;
                }

                public final String toString() {
                    return "[PrefixTransformer('" + prefix + "')]";
                }
            };
        }
    }

    public static NameTransformer chainedTransformer(NameTransformer t1, NameTransformer t2) {
        return new Chained(t1, t2);
    }
}
