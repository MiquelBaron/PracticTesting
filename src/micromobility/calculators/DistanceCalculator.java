package micromobility.calculators;

import data.GeographicPoint;

public class DistanceCalculator {
    private static final double EARTH_RADIUS_KM = 6371.0; // Valor para transformar latitud y longitud a distancia.

    // Calcula la distancia en kilómetros entre dos puntos geográficos.
    public static float calculateDistance(GeographicPoint point1, GeographicPoint point2) {
        double lat1Rad = Math.toRadians(point1.getLatitude());
        double lon1Rad = Math.toRadians(point1.getLongitude());
        double lat2Rad = Math.toRadians(point2.getLatitude());
        double lon2Rad = Math.toRadians(point2.getLongitude());

        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // Fórmula de Haversine
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = EARTH_RADIUS_KM * c;

        return (float) distance;
    }
}
