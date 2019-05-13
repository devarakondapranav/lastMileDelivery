from __future__ import print_function
from ortools.constraint_solver import pywrapcp
from ortools.constraint_solver import routing_enums_pb2
from flask import Flask,render_template, request
app = Flask(__name__)
import distances
import getCoordinates
from firebase import firebase
import capacity
import tbr
import random
from flask import Flask
from flask_mail import Mail, Message

app =Flask(__name__)
mail=Mail(app)

app.config['MAIL_SERVER']='smtp.gmail.com'
app.config['MAIL_PORT'] = 465
app.config['MAIL_USERNAME'] = 'atyamanudeep7@gmail.com'
app.config['MAIL_PASSWORD'] = 'deepu@98'
app.config['MAIL_USE_TLS'] = False
app.config['MAIL_USE_SSL'] = True
mail = Mail(app)

manager_map = []



def validateAddresses(a):
  for i in range(len(a)):
      a[i] = a[i].replace(',', '')
      a[i] = a[i].replace(' ', '+')
  return a

def validate(a):
  a = a.replace(',', '')
  a = a.replace(' ', '+')
  return a

ref = firebase.FirebaseApplication('https://sih-pepperfry.firebaseio.com', None)

def modify(s):
  i = 0
  nums = [str(i) for i in range(0, 10)]
  while(s[i] not in nums):
    i += 1
  return s[i:]



def updateDatabase(optimal_routes):
  pass


def create_data_model(num_vehicles, names, addresses, withTraffic):
  """Creates the data for the example."""
  data = {}
  data['API_key'] = ''#google maps api key here
  data['addresses'] = validateAddresses(addresses)
  # Array of distances between locations.
 
  _distances = distances.create_distance_matrix(data)

  print((distances.create_distance_matrix(data)))
  if(withTraffic):
    for i in range(len(_distances)):
      for j in range(len(_distances)):
        _distances[i][j] *= random.randint(1, 5)
      


  data["distances"] = _distances
  data["num_locations"] = len(_distances)
  data["num_vehicles"] = num_vehicles
  data["depot"] = 0
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

def add_distance_dimension(routing, distance_callback):
  """Add Global Span constraint"""
  distance = 'Distance'
  maximum_distance = 3000000  # Maximum distance per vehicle.
  routing.AddDimension(
      distance_callback,
      0,  # null slack
      maximum_distance,
      True,  # start cumul to zero
      distance)
  distance_dimension = routing.GetDimensionOrDie(distance)
  # Try to minimize the max distance among vehicles.
  distance_dimension.SetGlobalSpanCostCoefficient(100)
###########
# Printer #
###########
def print_solution(data, routing, assignment, coordinatesMap, names, addresses):
  """Print routes on console."""
  temp = []
  total_distance = 0  
  info_list = []
  tadd = []
  for vehicle_id in range(data["num_vehicles"]):
    index = routing.Start(vehicle_id)
    plan_output = 'Route for vehicle {}:\n'.format(vehicle_id)
    route_dist = 0
    info_string = ''
    details_string = ''

    while not routing.IsEnd(index):
      node_index = routing.IndexToNode(index)
      next_node_index = routing.IndexToNode(
        assignment.Value(routing.NextVar(index)))
      route_dist += routing.GetArcCostForVehicle(node_index, next_node_index, vehicle_id)
      plan_output += ' {0} ->'.format(node_index)
      tadd.append(addresses[node_index])
      info_string += coordinatesMap[node_index] + "->"
      details_string += names[node_index] + "::" + addresses[node_index] + "->"
      index = assignment.Value(routing.NextVar(index))
    plan_output += ' {}\n'.format(routing.IndexToNode(index)) +"*&*"
    plan_output += 'Distance of route: {}m<br>'.format(route_dist)
    manager_map.append(tadd.copy())
    print("Tadd",tadd)
    tadd.clear()

    print(plan_output)
    temp.append(plan_output)

    temp.append(info_string)
    temp.append("<br>")
    temp.append(details_string)
    temp.append("iamdelimiter")
    total_distance += route_dist

  print('Total distance of all routes: {}m'.format(total_distance))
  temp.append('total distance is : {}m'.format(total_distance))
  return " ".join(temp)
########
# Main #
########
def main_function(num_vehicles, names, addresses,coordinatesMap, withTraffic):
  """Entry point of the program"""
  # Instantiate the data problem.
  data = create_data_model(num_vehicles, names, addresses, withTraffic)
  # Create Routing Model
  routing = pywrapcp.RoutingModel(
      data["num_locations"],
      data["num_vehicles"],
      data["depot"])
  # Define weight of each edge
  distance_callback = create_distance_callback(data)
  routing.SetArcCostEvaluatorOfAllVehicles(distance_callback)
  add_distance_dimension(routing, distance_callback)
  # Setting first solution heuristic (cheapest addition).
  search_parameters = pywrapcp.RoutingModel.DefaultSearchParameters()
  search_parameters.first_solution_strategy = (
      routing_enums_pb2.FirstSolutionStrategy.PATH_CHEAPEST_ARC) # pylint: disable=no-member
  # Solve the problem.
  assignment = routing.SolveWithParameters(search_parameters)
  if assignment:
    return print_solution(data, routing, assignment, coordinatesMap, names, addresses)


@app.route('/')
def hello_world():
  print("hello guys")
  return render_template('table.html')

@app.route('/maps',methods=['POST','GET'])
def maps():


  print("manager can see map")
  result = request.form
  value = request.form.getlist('withTraffic')
  withTraffic = False
  if(len(value) == 1):
    withTraffic = True


  #return str(result['num_vehicles'])
  if(result["typeOfRouting"] == "distance"):
    names = []
    addresses = []
    coordinatesMap = {}
    message = ''
    for i in range(int(result["rowcount"])):
      names.append(result["name" + str(i)])
      addresses.append(result["address" + str(i)])
      locCoordinate = getCoordinates.send_request(validate(str(result["address" + str(i)])))
      coordinatesMap[i] = str(locCoordinate['lat']) + " " + str(locCoordinate['lng'])

    optimal_routes =  main_function(num_vehicles = int(result['num_vehicles']), names = names, addresses = addresses, coordinatesMap=coordinatesMap, withTraffic=withTraffic)
    
    new_routes = list(optimal_routes.split("iamdelimiter"))
    route_ll = []
    for route in new_routes:
      route = route.split("<br>")
      foobar = route[0].split("*&*")[0]
      route_ll.append(foobar)

    
    updateDatabase(optimal_routes)
    return render_template("result.html", optimal_routes=optimal_routes, addresses= addresses, new_routes=new_routes, route_ll=route_ll[:-1], message = '')
  
  elif(result["typeOfRouting"] == "capacity"):

    message = ''
    names = []
    addresses = []
    customer_capacities = []
    driver_capacities = []
    coordinatesMap = {}
    for i in range(int(result["num_vehicles"])):
      driver_capacities.append(int(result["driver_capacity" + str(i)]))
    for i in range(int(result["rowcount"])):
      names.append(result["name" + str(i)])
      addresses.append(result["address" + str(i)])
      customer_capacities.append(int(result["capacity"+ str(i)]))
      
      locCoordinate = getCoordinates.send_request(validate(str(result["address" + str(i)])))
      coordinatesMap[i] = str(locCoordinate['lat']) + " " + str(locCoordinate['lng'])


    
    optimal_routes =  capacity.main_function(num_vehicles = int(result['num_vehicles']), names = names, addresses = addresses, coordinatesMap=coordinatesMap, driver_capacities=driver_capacities, customer_capacities = customer_capacities)
    # print(manager_map)
    new_routes = list(optimal_routes.split("iamdelimiter"))[:-1]
    route_ll = []
    for route in new_routes:
      route = route.split("<br>")
      foobar = route[0].split("*&*")[0]
      route_ll.append(foobar)



    updateDatabase(optimal_routes)
    return render_template("result.html", optimal_routes=optimal_routes, addresses= addresses, new_routes = new_routes, route_ll = route_ll[:], message='')
   
  elif(result["typeOfRouting"] == "time"):

    names = []
    addresses = []
    customer_capacities = []
    driver_capacities = []
    start_times = []
    end_times = []
    coordinatesMap = {}
    
    for i in range(int(result["rowcount"])):
      names.append(result["name" + str(i)])
      addresses.append(result["address" + str(i)])
      customer_capacities.append(int(result["capacity"+ str(i)]))
      end_times.append((result["et" + str(i)]))
      start_times.append((result["st" + str(i)]))
      
      locCoordinate = getCoordinates.send_request(validate(str(result["address" + str(i)])))
      coordinatesMap[i] = str(locCoordinate['lat']) + " " + str(locCoordinate['lng'])


    
    optimal_routes =  tbr.main_function(num_vehicles = int(result['num_vehicles']), names = names, addresses = addresses, coordinatesMap=coordinatesMap, customer_capacities = customer_capacities, start_times=start_times, end_times=end_times)
    #return optimal_routes
    #optimal_routes =  capacity.main_function(num_vehicles = int(result['num_vehicles']), names = names, addresses = addresses, coordinatesMap=coordinatesMap, driver_capacities=driver_capacities, customer_capacities = customer_capacities)
    message = ""
    if(optimal_routes[0] == "@"):
      message = optimal_routes[1:105]
      optimal_routes = optimal_routes[105:]
      new_routes = list(optimal_routes.split("iamdelimiter"))
      route_ll = []
      for route in new_routes:
        route = route.split("<br>")
        foobar = route[0].split("*&*")[0]
        route_ll.append(foobar)

      updateDatabase(optimal_routes)
      return render_template("result.html", optimal_routes=optimal_routes, addresses= addresses, new_routes=new_routes, route_ll=route_ll[:-1], message = message)




    new_routes = list(optimal_routes.split("iamdelimiter"))
    route_ll = []
    for route in new_routes:
      route = route.split("<br>")

      locations = route[2].split("->")
      s_times = route[3].split("->")
      foobar=''
      for i in range(len(locations)-2):
        path="Starting from "+locations[i]+" to "+locations[i+1]+" at "+s_times[i]+"\n<br>";
        foobar += path 
      route_ll.append(foobar)



    updateDatabase(optimal_routes)
    if(len(route_ll) == 1):
      return render_template("result.html", optimal_routes=optimal_routes, addresses= addresses, new_routes = new_routes, route_ll = route_ll, message=message)
    else:
      return render_template("result.html", optimal_routes=optimal_routes, addresses= addresses, new_routes = new_routes, route_ll = route_ll, message=message)
   



  else:
    return " SDKJVb"

@app.route('/submitToDB',methods=['POST','GET'])
def submitToDB():

  result = request.form
  optimal_routes = result["optimal_routes"];

  optimal_routes_list = list(optimal_routes.split("iamdelimiter"))
  #count = len(ref.get('/routes/26Feb2019', None))
  la = []  
  for x in range(len(optimal_routes_list)-1):
    temp = optimal_routes_list[x]
    temp = list(temp.split("<br>"))
    #temp = temp[1]
    print('heyy ' + str(temp))
    obj = {}
    obj["assignedTo"] = None
    obj["status"] = "Ongoing"
    obj["coordinates"] =  (str(temp[1]).strip()[:-2])


    obj["customer_details"] = str(temp[2]).strip()



    a = ref.post('/routes/', obj)
    la.append(a)
  return "saved to database " + str(la)

@app.route('/rendermap',methods=['POST','GET'])
def rendermap():

# res = request.forrm
  t=[]
    # manager_map = [["Hyderabad","Secunderbad","Lalapet"],["Hyderabad","Mehdipatnam","Gandipet"]]
  t=manager_map.copy()
  print(t)
  manager_map.clear()
# result=[["Shamirpet","Hyderabad","Banjara hills"],["Shamirpet","Hakimper"],["Shamirpet","Gandipet"]]
# print(result[0][0])
  return render_template("maps.html",result = t)

@app.route("/sendMail")
def sendmail():
  msg = Message('Hello', sender = 'atyamanudeep7@gmail.com', recipients = ['atyam.anudeep@gmail.com'])
  msg.body = "Your order will be delivered today ,please go to the link mentioned '127.0.0.1:5000/requestChange' for rescheduling the delivery and you can also see the status of your delivery here.. '127.0.0.1:5000/liveloc' "
  mail.send(msg)
     # return render_template('mailsent.html')
  return render_template('mail_confirmation.html')

@app.route("/requestChange")
def change():
  return render_template('requestdone.html')

@app.route("/liveloc")
def liveloc():
  print('showing live locations')
  return render_template('index1.html') 

if __name__ == '__main__':
   app.run( debug=True)
