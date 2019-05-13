package com.nihanth.maproutebetweenmarkers;

public class Route {
    public RouteExtension routeExtension;
    public Route(RouteExtension routeExtension)
    {
        this.routeExtension = routeExtension;
    }

    public RouteExtension getRouteExtension() {
        return routeExtension;
    }
}
