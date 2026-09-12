# Jraffic

Jraffic is a traffic simulation project that represents a road intersection with vehicles and traffic lights.

The goal of the project is to simulate traffic while avoiding collisions and dynamically managing traffic lights depending on the congestion of each lane.


## Features

* Two intersecting roads with one lane in each direction.
* Traffic lights with red and green states.
* Vehicles can:

  * Go straight
  * Turn left
  * Turn right
* Each vehicle has a fixed speed and route.
* Vehicles keep a safe distance from each other.
* Traffic lights adapt to traffic congestion.
* Vehicles can be spawned using the keyboard.
* Vehicle colors indicate their selected route.
* `Esc` closes the simulation.

## Controls

| Key   | Action                                  |
| ----- | --------------------------------------- |
| `↑`   | Spawn a vehicle coming from the South   |
| `↓`   | Spawn a vehicle coming from the North   |
| `→`   | Spawn a vehicle coming from the West    |
| `←`   | Spawn a vehicle coming from the East    |
| `R`   | Spawn a vehicle from a random direction |
| `Esc` | Exit the simulation                     |

Vehicles are created with a safe distance between them to prevent collisions.

## Traffic Management

The simulation dynamically manages the traffic lights according to the number of vehicles waiting on each lane.

The maximum capacity of a lane is calculated using:

```text
capacity = floor(lane_length / (vehicle_length + safety_gap))
```

When a lane becomes highly congested, the traffic light system can adapt its green time to reduce the queue and prevent overflow.



## How to Run

Make sure you have the required Java environment installed.

Build and run the project with:

```bash
make
```

or, if the project provides a specific run target:

```bash
make run
```

To clean the generated files:

```bash
make clean
```

## Project Structure

The project contains the main components needed for:

* Road and intersection management
* Traffic light control
* Vehicle movement
* Collision and safety-distance management
* Keyboard controls
* Traffic simulation and visualization

## Group

* Ahmed Talbi
* Mohamed Rida
* Mohammed Elbouabdellaoui

## Objective

The main objective of Jraffic is to create a simple but realistic traffic simulation where vehicles can move safely through an intersection while the traffic light system adapts to traffic congestion.

## notes 

``HashMap``: key → hashCode() → bucket → find key     
``EnumMap``: enum → ordinal/index → direct array access
