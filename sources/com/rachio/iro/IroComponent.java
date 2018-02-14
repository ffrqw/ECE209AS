package com.rachio.iro;

public interface IroComponent extends IroGraph {

    public static final class Initializer {
        static IroGraph init(IroApplication app) {
            return DaggerIroComponent.builder().iroAppModule(new IroAppModule(app)).build();
        }

        private Initializer() {
        }
    }
}
