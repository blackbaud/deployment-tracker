package com.blackbaud.deployment.api;

import static com.blackbaud.deployment.api.ClientARandom.aRandom

class RandomReleasePlanBuilder extends ReleasePlan.ReleasePlanBuilder{

    public RandomReleasePlanBuilder() {
        notes(aRandom.text(20))
    }
}
