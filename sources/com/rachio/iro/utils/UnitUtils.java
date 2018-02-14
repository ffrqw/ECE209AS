package com.rachio.iro.utils;

import android.content.Context;
import com.rachio.iro.model.user.User;
import com.rachio.iro.model.user.User.DisplayUnit;

public class UnitUtils {
    public static final double convertTempToUserUnits(User user, double degrees) {
        return user.displayUnit == DisplayUnit.US ? (1.7999999523162842d * degrees) + 32.0d : degrees;
    }

    public static final double convertGallonsToUserUnits(User user, double gallons) {
        return convertGallonsToUserUnits(user.displayUnit, gallons);
    }

    public static final double convertGallonsToUserUnits(DisplayUnit displayUnit, double gallons) {
        if (displayUnit == DisplayUnit.METRIC) {
            return gallons * 3.78541178d;
        }
        return gallons;
    }

    public static final double convertCubicFeetToUserUnits(User user, double cubicFeet) {
        if (user.displayUnit == DisplayUnit.METRIC) {
            return cubicFeet * 0.0283168466d;
        }
        return cubicFeet;
    }

    public static final String getNameOfWaterUnits(User user) {
        return getNameOfWaterUnits(user.displayUnit);
    }

    public static final String getNameOfWaterUnits(DisplayUnit displayUnit) {
        switch (displayUnit) {
            case METRIC:
                return "Liters";
            case US:
                return "Gallons";
            default:
                throw new RuntimeException();
        }
    }

    public static final String getNameOfWaterVolumeUnits(User user) {
        switch (user.displayUnit) {
            case METRIC:
                return "Cubic Meters";
            case US:
                return "Cubic Feet";
            default:
                throw new RuntimeException();
        }
    }

    public static final String getNameOfAreaUnits(User user) {
        switch (user.displayUnit) {
            case METRIC:
                return "m²";
            case US:
                return "ft²";
            default:
                throw new RuntimeException();
        }
    }

    public static final String getNameOfWindSpeedUnits(User user) {
        switch (user.displayUnit) {
            case METRIC:
                return "kph";
            case US:
                return "mph";
            default:
                throw new RuntimeException();
        }
    }

    public static final double convertMilesToUserUnits(User user, double miles) {
        return convertMilesToUserUnits(user.displayUnit, miles);
    }

    public static final double convertMilesToUserUnits(DisplayUnit displayUnit, double miles) {
        switch (displayUnit) {
            case METRIC:
                return miles * 1.609344d;
            case US:
                return miles;
            default:
                throw new RuntimeException();
        }
    }

    public static final double convertMilesPerHourToUserUnits(User user, double mph) {
        switch (user.displayUnit) {
            case METRIC:
                return mph * 1.60934d;
            case US:
                return mph;
            default:
                throw new RuntimeException();
        }
    }

    public static final double convertMetersToMiles(double meters) {
        return 6.21371E-4d * meters;
    }

    public static final String getNameOfDistanceUnits(User user) {
        return getNameOfDistanceUnits(user.displayUnit);
    }

    public static final String getNameOfDistanceUnits(DisplayUnit displayUnit) {
        switch (displayUnit) {
            case METRIC:
                return "km";
            case US:
                return "mi";
            default:
                throw new RuntimeException();
        }
    }

    public static final String getPrecipUnitName(User user) {
        switch (user.displayUnit) {
            case METRIC:
                return "mm/hour";
            case US:
                return "in/hour";
            default:
                throw new RuntimeException();
        }
    }

    public static final String getMinorLengthUnitName(User user) {
        switch (user.displayUnit) {
            case METRIC:
                return "cm";
            case US:
                return "in";
            default:
                throw new RuntimeException();
        }
    }

    public static final double convertSquareYardsToUserUnits(User user, double squareYards) {
        switch (user.displayUnit) {
            case METRIC:
                return squareYards * 0.09290304d;
            case US:
                return squareYards;
            default:
                throw new RuntimeException();
        }
    }

    public static final double converUserUnitsToSquareYards(User user, double userUnits) {
        switch (user.displayUnit) {
            case METRIC:
                return userUnits / 0.09290304d;
            case US:
                return userUnits;
            default:
                throw new RuntimeException();
        }
    }

    public static final double convertInchesToUserUnits(User user, double inches) {
        switch (user.displayUnit) {
            case METRIC:
                return inches * 2.54d;
            case US:
                return inches;
            default:
                throw new RuntimeException();
        }
    }

    public static final double convertPrecipToUserUnits(User user, double inches) {
        switch (user.displayUnit) {
            case METRIC:
                return (2.54d * inches) * 10.0d;
            case US:
                return inches;
            default:
                throw new RuntimeException();
        }
    }

    public static final double convertUserUnitsToInches(User user, double userUnits) {
        switch (user.displayUnit) {
            case METRIC:
                return userUnits / 2.54d;
            case US:
                return userUnits;
            default:
                throw new RuntimeException();
        }
    }

    public static final double convertUserUnitsToPrecip(User user, double userUnits) {
        switch (user.displayUnit) {
            case METRIC:
                return (userUnits / 2.54d) / 10.0d;
            case US:
                return userUnits;
            default:
                throw new RuntimeException();
        }
    }

    public static final int toDp(Context context, int pixels) {
        return (int) ((((float) pixels) * context.getResources().getDisplayMetrics().density) + 0.5f);
    }
}
