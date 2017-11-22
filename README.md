# Tools & Env CW

In an attempt to answer the most important question of all time, **"How big is a large commit and what impact do they have on
software development?"**, here is the code base to make use of the Mining Repository API.

## Installation and use

### DataGenerator

The first part requires use to filter data from the massive zip file. Code for this can be found in the `DataGenerator` folder.

We use ant to build the project and make use of an uberjar with all the required files.

The only file you should need to change (for now) is,
`DataGenerator/src/main/java/Filter.java`.

You need to update the _directory path_ inside the Filter.java folder to point to your massive zip folder.

**TODO:** update this to use args

To run, simply call `ant` in `/DataGenerator`.



## To Do List

- Filter out data from dataset

-> First step is to classify commits into size categories
-> Filter for edit events that precede a version control event

- Statistcal analysis
- Write Report
