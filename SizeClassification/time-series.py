import json
import matplotlib.pyplot as plt
import numpy as np
import os

lim1 = 6
lim2 = 240
lim3 = 591
lim4 = 942

# small_test_pass = 0
# medium_test_pass = 0
# large_test_pass = 0

small_test_fail = 0
medium_test_fail = 0
large_test_fail = 0

# small_build_pass = 0
# medium_build_pass = 0
# large_build_pass = 0

small_build_fail = 0
medium_build_fail = 0
large_build_fail = 0

def loop_through_files():
    for subdir, dirs, files in os.walk('output'):
        for file in files:
            #print (os.path.join(subdir, file))
            data = json.load(open(os.path.join(subdir, file)))
            getFreq(data, os.path.join(subdir, file))

def getFreq(data, name):
    small_changes = medium_changes = large_changes = 0

    for key in data:
        event = data[key]["event_type"]

        if event == "BuildEvent":
            d = data[key]["specific_data"]["ListOfBuilds"]

            for k in d:
                if data[key]["specific_data"]["ListOfBuilds"][k]["Successful"] == False:
                    classify_size_of_change_test(small_changes, medium_changes, large_changes)
                    small_changes = medium_changes = large_changes = 0

        elif event == "TestRunEvent":
            d = data[key]["specific_data"]["ListOfTests"]
            for k in d:
                if data[key]["specific_data"]["ListOfTests"][k]["Result"] != "Success":
                    classify_size_of_change_build(small_changes, medium_changes, large_changes)
                    small_changes = medium_changes = large_changes = 0

        elif event == "EditEvent":
            d = data[key]["specific_data"]["SizeOfChanges"]
            if d >= lim1 and d < lim2:
                small_changes += 1
            elif d >= lim2 and d < lim3:
                medium_changes += 1
            elif d >= lim3 and d < lim4:
                large_changes += 1

def classify_size_of_change_test(small, medium, large):
    global small_test_fail, medium_test_fail, large_test_fail

    if large > medium and large > small:
        large_test_fail += 1
    elif medium > large and medium > small:
        medium_test_fail += 1
    else:
        small_test_fail += 1

def classify_size_of_change_build(small, medium, large):
    global small_build_fail, medium_build_fail, large_build_fail

    if large > medium and large > small:
        large_build_fail += 1
    elif medium > large and medium > small:
        medium_build_fail += 1
    else:
        small_build_fail += 1

def draw_pie_charts():
    labels = "Small", "Medium", "Large"
    sizes = [small_test_fail, medium_test_fail, large_test_fail]
    explode = (0,0,0)
    plt.pie(sizes, explode=explode, labels=labels)
    plt.axis('equal')
    plt.show()

    labels = "Small", "Medium", "Large"
    sizes = [small_build_fail, medium_build_fail, large_build_fail]
    explode = (0,0,0)
    plt.pie(sizes, explode=explode, labels=labels)
    plt.axis('equal')
    plt.show()

loop_through_files()
draw_pie_charts()
