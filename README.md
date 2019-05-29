# Weighing.ixi

## Abstract

**Weighing.ixi** is an [IXI (IOTA eXtending Interface) module](https://github.com/iotaledger/ixi) for the [Iota Controlled agenT (Ict)](https://github.com/iotaledger/ict).
It extends the core client with the functionality to calculate the number of edges incident to a vertex which match a set of attributes within a specific time window. [1] Weighing.ixi makes use of Graph.ixi and Timestamping.ixi [2][3].

[1] [Specification of Weighing.ixi](https://github.com/iotaledger/omega-docs/blob/master/ixi/weighing/Spec.md)<br>
[2] [Repository of Graph.ixi](https://github.com/iotaledger/graph.ixi)<br>
[3] [Repository of Timestamping.ixi](https://github.com/iotaledger/timestamping.ixi)

## Installation

### Step 1: Install Ict

Please find instructions on [iotaledger/ict](https://github.com/iotaledger/ict#installation).

Make sure you are connected to the main network and not to an island, otherwise you won't be able to message anyone in the main network.

### Step 2: Get Weighing.ixi

There are two ways to do this:

#### Simple Method

Go to [releases](https://github.com/iotaledger/weighing.ixi/releases) and download the **weighing-{VERSION}.jar**
from the most recent release.

#### Advanced Method

You can also build the .jar file from the source code yourself. You will need **Git** and **Gradle**.

```shell
# download the source code from github to your local machine
git clone https://github.com/iotaledger/weighing.ixi
# if you don't have git, you can also do this instead:
#   wget https://github.com/iotaledger/weighing.ixi/archive/master.zip
#   unzip master.zip

# change into the just created local copy of the repository
cd weighing.ixi

# build the weighing-{VERSION}.jar file
gradle ixi
```

### Step 3: Install Weighing.ixi
Move weighing-{VERSION}.jar to the **modules/** directory of your Ict:
```shell
mv weighing-{VERSION}.jar ict/modules
```

### Step 4: Run Ict
Switch to Ict directory and run:
```shell
java -jar ict-{VERSION}.jar
```