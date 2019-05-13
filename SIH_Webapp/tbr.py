"""Capacitated Vehicle Routing Problem with Time Windows (CVRPTW).
"""
from __future__ import print_function
from ortools.constraint_solver import pywrapcp
from ortools.constraint_solver import routing_enums_pb2
import distances
import routing as routing_file



from datetime import datetime

def validateAddresses(a):
  for i in range(len(a)):
      a[i] = a[i].replace(',', '')
      a[i] = a[i].replace(' ', '+')
  return a

def validate(a):
  a = a.replace(',', '')
  a = a.replace(' ', '+')
  return a


def getDif(a, s):
    ch = int(a.split(":")[0])
    cm = int(a.split(":")[1])

    uh = int(s.split(":")[0])
    um = int(s.split(":")[1])

    return int((uh-ch)*60 + (um-cm))

def addTime(a, s):
    
    uh = int(s.split(":")[0])*60
    um = int(s.split(":")[1])

    return str(int((uh+um+a)/60))+":"+str(int((uh+um+a)%60))

def getDist(data, a, b):
  return data["distances"][a][b]

def getTime(a):
  return str((a)/60)+":"+str((a)%60)

###########################
# Problem Data Definition #
###########################
def create_data_model(num_vehicles, names, addresses, coordinatesMap,customer_capacities, start_times, end_times):
  """Creates the data for the example."""
  data = {}
  # Array of distances between locations.
  data = {}
  data['API_key'] = '' #google maps api here
  data['addresses'] = validateAddresses(addresses)
  # Array of distances between locations.
  _distances = distances.create_distance_matrix(data)

  demands = customer_capacities
  data["starting_times"]=start_times[0]

  n = datetime.now()

  ch = n.hour
  cm = n.minute
  

  time_windows = [(getDif(start_times[0], start_times[i]), getDif(start_times[0], end_times[i])) for i in range(len(start_times)) ]

  data["distances"] = _distances
  data["num_locations"] = len(_distances)
  data["num_vehicles"] = num_vehicles
  data["depot"] = 0
  data["demands"] = demands
  data["time_windows"] = time_windows
  data["time_per_demand_unit"] = 5
  data["vehicle_speed"] = 100
  return data
#######################
# Problem Constraints #
#######################
def create_distance_callback(data):
  """Creates callback to return distance between points."""
  distances = data["distances"]

  def distance_callback(from_node, to_node):
    """Returns the manhattan distance between the two nodes"""
    return distances[from_node][to_node]

  return distance_callback

def create_time_callback(data):
  """Creates callback to get total times between locations."""
  def service_time(node):
    """Gets the service time for the specified location."""
    return data["demands"][node] * data["time_per_demand_unit"]

  def travel_time(from_node, to_node):
    """Gets the travel times between two locations."""
    travel_time = data["distances"][from_node][to_node] / data["vehicle_speed"]
    return travel_time

  def time_callback(from_node, to_node):
    """Returns the total time between the two nodes"""
    serv_time = service_time(from_node)
    trav_time = travel_time(from_node, to_node)
    return serv_time + trav_time

  return time_callback

def add_time_window_constraints(routing, data, time_callback):
  """Add time window constraints."""
  time = "Time"
  horizon = 120000
  routing.AddDimension(
    time_callback,
    horizon, # allow waiting time
    horizon, # maximum time per vehicle
    False, # Don't force start cumul to zero. This doesn't have any effect in this example,
           # since the depot has a start window of (0, 0).
    time)

  time_dimension = routing.GetDimensionOrDie(time)
  for location_node, location_time_window in enumerate(data["time_windows"]):
        index = routing.NodeToIndex(location_node)
        time_dimension.CumulVar(index).SetRange(location_time_window[0], location_time_window[1])

###########
# Printer #
###########
def print_solution(data, routing, assignment, coordinatesMap, names, addresses, manager_map):
  """Prints assignment on console"""
  # Inspect solution.

  


  temp = []
  info_list = []
  total_dist = 0
  time_dimension = routing.GetDimensionOrDie('Time')
  total_dist = 0
  time_matrix = 0

  

  for vehicle_id in range(data["num_vehicles"]):
    index = routing.Start(vehicle_id)
    plan_output = 'Route for vehicle {0}:\n'.format(vehicle_id)
    route_dist = 0
    info_string = ''
    details_string = ''
    isFirst = True
    time_string = ''
    prev = None
    tadd = []

    while not routing.IsEnd(index):
      node_index = routing.IndexToNode(index)
      next_node_index = routing.IndexToNode(
      assignment.Value(routing.NextVar(index)))
      route_dist += routing.GetArcCostForVehicle(node_index, next_node_index, vehicle_id)
      time_var = time_dimension.CumulVar(index)
      time_min = assignment.Min(time_var)
      time_max = assignment.Max(time_var)
      plan_output += ' {0} Time({1},{2}) ->'.format(node_index, time_min, time_max)
      info_string += coordinatesMap[node_index] + "->"
      tadd.append(addresses[node_index])
      details_string += names[node_index] + "::" + addresses[node_index] + "->"
      index = assignment.Value(routing.NextVar(index))
      if(isFirst):
        isFirst = False
        prev = node_index
        continue
      else:
        diff = time_min - int(getDist(data, node_index, prev)/data["vehicle_speed"])
        t = addTime(diff,data["starting_times"]) 
        time_string += t+"->"
        prev=node_index



    manager_map.append(tadd.copy())
    print("Tadd",tadd)
    tadd.clear()


    node_index = routing.IndexToNode(index)
    time_var = time_dimension.CumulVar(index)
    route_time = assignment.Value(time_var)
    time_min = assignment.Min(time_var)
    time_max = assignment.Max(time_var)
    total_dist += route_dist
    time_matrix += route_time
    plan_output += ' {0} Time({1},{2})\n'.format(node_index, time_min, time_max)

    plan_output += 'Distance of the route: {0} m\n'.format(route_dist)
    plan_output += 'Time of the route: {0} min\n<br>'.format(route_time)

    temp.append(plan_output)
    temp.append("*&*")
    temp.append(info_string)
    temp.append("<br>")
    temp.append(details_string)
    temp.append("<br>")
    temp.append(time_string)
    temp.append("iamdelimiter")
    print(plan_output)
  print('Total Distance of all routes: {0} m'.format(total_dist))
  print('Total Time of all routes: {0} min'.format(time_matrix))
  return " ".join(temp[:-1])


########
# Main #
########
def main_function(num_vehicles, names, addresses, coordinatesMap, customer_capacities, start_times, end_times):
  """Entry point of the program"""
  data = create_data_model(num_vehicles, names, addresses, coordinatesMap, customer_capacities, start_times, end_times)

  #return data
  # Create Routing Model
  #return data
  routing = pywrapcp.RoutingModel(data["num_locations"], data["num_vehicles"], data["depot"])
  # Define weight of each edge
  distance_callback = create_distance_callback(data)
  routing.SetArcCostEvaluatorOfAllVehicles(distance_callback)
  # Add Time Window constraint
  time_callback = create_time_callback(data)
  add_time_window_constraints(routing, data, time_callback)
  # Setting first solution heuristic (cheapest addition).
  search_parameters = pywrapcp.RoutingModel.DefaultSearchParameters()
  search_parameters.first_solution_strategy = (
    routing_enums_pb2.FirstSolutionStrategy.PATH_CHEAPEST_ARC)
  # Solve the problem.
  assignment = routing.SolveWithParameters(search_parameters)
  if assignment:
    return print_solution(data, routing, assignment, coordinatesMap, names, addresses, manager_map)
    
  else:
    return '@Delivery route with the given constraints is not feasible. Showing the optimal path based on distances. ' + routing_file.main_function(num_vehicles, names, addresses, coordinatesMap, False)

if __name__ == '__main__':
  main_function()