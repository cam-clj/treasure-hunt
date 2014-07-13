(ns cam-clj.treasure-hunt.util.interval)

(defn seconds-part
  [n]
  (mod n 60))

(defn minutes-part
  [n]
  (mod (quot n 60) 60))

(defn hours-part
  [n]
  (quot n 3600))

(defn seconds->str
  [secs]
  (format "%d hours, %d minutes and %d seconds"
          (hours-part secs)
          (minutes-part secs)
          (seconds-part secs)))
