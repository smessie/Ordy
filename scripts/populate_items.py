#!/bin/python3

import mysql.connector as mariadb

mariadb_connection = mariadb.connect(host='ordy.ga', user='ordy_remote_dev', password='Pl31s3d0n0tNuk3Th1sD1t1B1s3Y33T', database='ordy_remote_dev', buffered=True)
cursor = mariadb_connection.cursor()
cuisine_id_map = {}

def insert_items():
    for cuisine in cuisine_id_map:
        # Read all predefined items from .txt-files
        if not cuisine == "unknown":
            item_file = open("item_files/" + cuisine + ".txt")
            item_list = [item.rstrip() for item in item_file.readlines()]
            for item in item_list:
                cursor.execute("INSERT INTO items(name) VALUES (%s)", tuple([item]))
                item_id = cursor.lastrowid
                # Create additional entries in cuisines_items and items_cuisines tables
                cursor.execute("INSERT INTO cuisines_items(cuisine_id, items_id) VALUES (%s, %s)", (cuisine_id_map[cuisine], item_id))
                cursor.execute("INSERT INTO items_cuisines(item_id, cuisines_id) VALUES (%s, %s)", (item_id, cuisine_id_map[cuisine]))
                mariadb_connection.commit()
            item_file.close()

#### TESTING PURPOSES ####
def get_items():
  cursor.execute("SELECT id, name FROM items")
  for id, name in cursor:
    print("item: {}, {}".format(id, name))

def get_cuisines_items():
    cursor.execute("SELECT cuisine_id, items_id FROM cuisines_items")
    for cuisine, item in cursor:
        print("cuisine item: {}, {}".format(cuisine, item))

def get_item_cuisine():
    cursor.execute("SELECT item_id, cuisines_id FROM items_cuisines")
    for item, cuisine in cursor:
        print("item cuisine: {}, {}".format(item, cuisine))

def fill_cuisine_map():
  cursor.execute("SELECT id, name FROM cuisines")
  for id, name in cursor:
    cuisine_id_map[name] = id

def delete_cuisines_items():
    cursor.execute("DELETE FROM cuisines_items")
    mariadb_connection.commit()

def delete_items_cuisines():
    cursor.execute("DELETE FROM items_cuisines")
    mariadb_connection.commit()

def delete_items():
    cursor.execute("DELETE FROM items")
    mariadb_connection.commit()

def delete_all():
    delete_cuisines_items()
    delete_items_cuisines()
    delete_items()

# Refresh all items in db, won't work if there are orders with those items
fill_cuisine_map()
delete_all()
insert_items()
get_items()
get_item_cuisine()
get_cuisines_items()
cursor.close()
mariadb_connection.close()