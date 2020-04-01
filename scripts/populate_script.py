#!/bin/python3

import json
import os
import mysql.connector as mariadb

mariadb_connection = mariadb.connect(host='ordy.ga',
                                     user='ordy_remote_dev',
                                     password='Pl31s3d0n0tNuk3Th1sD1t1B1s3Y33T',
                                     database='ordy_remote_dev', buffered=True)
cursor = mariadb_connection.cursor()
cuisine_id_map = {}
coordinate_margin = 0.001


def location_is_registered(lat, lon, name):
    min_lat, max_lat, min_lon, max_lon = lat - coordinate_margin, lat + coordinate_margin, lon - coordinate_margin, lon + coordinate_margin

    cursor.execute(
        "SELECT name, COUNT(*) FROM locations WHERE latitude > %s AND longitude > %s AND latitude < %s AND longitude < %s AND name = %s GROUP BY name",
        (min_lat, min_lon, max_lat, max_lon, name))
    count = cursor.rowcount
    return count > 0


def update_json():
    # Collects all restaurants within borders south, west, north and east respectively
    os.system(
        '''curl --globoff -o osm_output.json "http://overpass-api.de/api/interpreter?data=[out:json];node['amenity'='fast_food'](50.975798,3.582471,51.144645,3.813184);out;"''')


def populate():
    with open('osm_output.json') as f:
        locationDict = json.load(f)
        for i in range(len(locationDict["elements"])):
            loc = locationDict["elements"][i]

            tags = loc["tags"]
            # Filter out locations without name
            if "name" in tags:
                name = tags["name"]
                lat = loc["lat"]
                lon = loc["lon"]
                if not location_is_registered(lat, lon, name):
                    address = None
                    if "addr:city" in tags and "addr:housenumber" in tags and "addr:street" in tags:
                        address = tags["addr:street"] + " " + tags["addr:housenumber"] + ", " + tags[
                            "addr:postcode"] + " " + tags["addr:city"]
                    private = False
                    if "access" in tags:
                        if tags["access"] == 'private':
                            private = True
                    cuisine = cuisine_id_map["unknown"]
                    if "cuisine" in tags:
                        cuisine = get_cuisine_id(tags["cuisine"])

                    values = (name, lat, lon, address, private, cuisine)
                    insert = "INSERT INTO locations (name, latitude, longitude, address, private, cuisine_id) VALUES (%s,%s,%s,%s,%s,%s)"
                    cursor.execute(insert, values)
                    mariadb_connection.commit()


# Determines cuisine-id, taking different spelling methods into account
def get_cuisine_id(name: str):
    name = name.lower()
    if name == "kebab" or name == "shawarma" or name == "pitta" or name == "pita":
        return cuisine_id_map["kebab"]
    elif name == "fries" or name == "frituur" or name == "friture":
        return cuisine_id_map["fries"]
    elif name == "pizza" or name == "italian":
        return cuisine_id_map["pizza"]
    elif name == "sandwiches" or name == "sandwichbar" or name == "sandwich":
        return cuisine_id_map["sandwiches"]
    elif name == "chinese" or name == "asian":
        return cuisine_id_map["chinese"]
    elif name == "thai":
        return cuisine_id_map["thai"]
    elif name == "greek":
        return cuisine_id_map["greek"]
    elif name == "donut" or name == "donuts":
        return cuisine_id_map["donuts"]
    elif name == "hamburger" or name == "hamburgers" or name == "burger" or name == "burgers":
        return cuisine_id_map["burgers"]
    elif name == "sushi" or name == "fish" or name == "japanese":
        return cuisine_id_map["sushi"]
    elif name == "wok":
        return cuisine_id_map["wok"]
    else:
        return cuisine_id_map["unknown"]


def update_database():
    test_cuisine()
    update_json()
    populate()


#### TESTING PURPOSES ####
def get_content():
    cursor.execute("SELECT id, name, latitude, longitude, address, private, cuisine_id FROM locations")
    for id, name, latitude, longitude, address, private, cuisine in cursor:
        print("id: {}, Location: {}, latitude: {}, longitude: {}, address: {}, private: {}, cuisine: {}"
              .format(id, name, latitude, longitude, address, private, cuisine))


def get_users():
    cursor.execute("SELECT username FROM users")
    for name in cursor:
        print("User: {}".format(name))


def get_cuisines():
    cursor.execute("SELECT id, name FROM cuisines")
    for id, name in cursor:
        print("Cuisine: {}, {}".format(id, name))


def delete_cuisines():
    cursor.execute("DELETE FROM cuisines")
    mariadb_connection.commit()


def test_cuisine():
    # All recognized cuisines, with their id == index in list
    cuisine_names = ['kebab', 'fries', 'pizza', 'sandwiches', 'chinese', 'thai', 'greek', 'donuts', 'burgers', 'sushi',
                     'wok', 'unknown']

    for i in range(len(cuisine_names)):
        cursor.execute("SELECT id FROM cuisines WHERE name = %s", tuple([cuisine_names[i]]))
        row = cursor.fetchone()
        if row is None:
            cursor.execute("INSERT INTO cuisines(name) VALUES(%s)", tuple([cuisine_names[i]]))
            cuisine_id_map[cuisine_names[i]] = cursor.lastrowid
            mariadb_connection.commit()
        else:
            cuisine_id_map[cuisine_names[i]] = row[0]
    get_cuisines()
    print(cuisine_id_map)


def delete_locations():
    cursor.execute("DELETE FROM locations")
    mariadb_connection.commit()


def fill_cuisine_map():
    cursor.execute("SELECT id, name FROM cuisines")
    for id, name in cursor:
        cuisine_id_map[name] = id


update_database()
cursor.close()
mariadb_connection.close()
