(ns cam-clj.treasure-hunt.util.maze
  (:require [clojure.set :as set]))

;; The north-west corner of the grid as at [0 0]
(def direction->delta {:north [0 -1] :east [1 0] :south [0 1] :west [-1 0]})

(defn initial-grid
  "Create a grid with m columns and n rows. We store the grid as an
   adjacency list - each element in the m x n matrix stores the set of
   that cell's neighbours. We start with an initial grid that is all
   walls, i.e. every element is the empty set."
  [m n]
  (into [] (repeat n (into [] (repeat m #{})))))

(defn num-cols
  "Return the number of columns in the grid"
  [grid]
  (count (first grid)))

(defn num-rows
  "Return the number of rows in the grid"
  [grid]
  (count grid))

(defn apply-delta-direction
  [direction [x y]]
  (let [[delta-x delta-y] (direction->delta direction)]
    [(+ x delta-x) (+ y delta-y)]))

(defn in-grid?
  "Return true if the point [x y] is inside the grid, otherwise false."
  [grid [x y]]
  (and (< -1 x (num-cols grid))
       (< -1 y (num-rows grid))))

(defn remove-wall
  "Return a new grid with the wall removed."
  [grid wall]
  (let [[cell-a cell-b] (seq wall)]
    (-> grid
        (update-in cell-a conj cell-b)
        (update-in cell-b conj cell-a))))

(defn neighbours
  "Return the set of neighbours of [x y] in the grid."
  [grid [x y]]
  (get-in grid [x y]))

(defn directions-from
  "Return the possible directions to walk from [x y] in the grid."
  [grid [x y]]
  (let [neighbour? (neighbours grid [x y])]
    (for [[direction [delta-x delta-y]] direction->delta
          :let [x' (+ x delta-x) y' (+ y delta-y)]
          :when (neighbour? [x' y'])]
      direction)))

(defn cell-walls
  "Return the set of walls bounding [x y] in the grid. We represent a wall
   between [x y] and [x' y'] as the set #{[x y] [x' y']}."
  [grid [x y]]
  (let [neighbour? (neighbours grid [x y])]
    (reduce (fn [walls [delta-x delta-y]]
              (let [x' (+ x delta-x)
                    y' (+ y delta-y)]
                (if (and (in-grid? grid [x' y'])
                         (not (neighbour? [x' y'])))
                  (conj walls #{[x y] [x' y']})
                  walls)))
            #{}
            (vals direction->delta))))

(defn random-prim-maze
  "Generate an m x n maze using the randomized Prim's algorithm,
  http://en.wikipedia.org/wiki/Maze_generation_algorithm#Randomized_Prim.27s_algorithm"
  [m n]
  (let [starting-position [(rand-int m) (rand-int n)]]
    (loop [grid    (initial-grid m n)
           visited #{starting-position}
           walls   (cell-walls grid starting-position)]
      (if (empty? walls)
        grid
        (let [wall (rand-nth (vec walls))
              [cell-a cell-b] (seq wall)]
          (cond
           (and (visited cell-a) (visited cell-b)) (recur grid visited (disj walls wall))
           (visited cell-a) (recur (remove-wall grid wall)
                                   (conj visited cell-b)
                                   (disj (set/union walls (cell-walls grid cell-b)) wall))
           (visited cell-b) (recur (remove-wall grid wall)
                                   (conj visited cell-a)
                                   (disj (set/union walls (cell-walls grid cell-a)) wall))))))))

(defn eliminate-random-walls
  "Eliminate walls from maze with probability p"
  [maze p]
  (let [walls (distinct (mapcat (partial cell-walls maze)
                                (for [x (range (num-cols maze))
                                      y (range (num-rows maze))]
                                  [x y])))]
    (reduce (fn [maze wall]
              (if (< (rand) p)
                (remove-wall maze wall)
                maze))
            maze
            walls)))

(defn generate-maze
  "Generate an m x n maze. If loop-factor (0 < loop-factor < 1)
   is specified, that proportion of edges will be removed to create loops."
  [m n & [loop-factor]]
  {:pre [(> m 1) (> n 1) (or (nil? loop-factor) (< 0 loop-factor 1))]}
  (let [maze (random-prim-maze m n)]
    (if loop-factor
      (eliminate-random-walls maze loop-factor)
      maze)))
