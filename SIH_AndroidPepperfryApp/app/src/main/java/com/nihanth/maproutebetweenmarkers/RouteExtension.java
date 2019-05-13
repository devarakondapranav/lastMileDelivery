package com.nihanth.maproutebetweenmarkers;

public class RouteExtension {
    public String status;
    public String route;
    public RouteExtension(String status, String route)
    {
        this.status = status;
        this.route = route;
    }

    public String getRoute() {
        return route;
    }

    public String getStatus() {
        return status;
    }
}
