# search.py
# ---------
# Licensing Information: Please do not distribute or publish solutions to this
# project. You are free to use and extend these projects for educational
# purposes. The Pacman AI projects were developed at UC Berkeley, primarily by
# John DeNero (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# For more info, see http://inst.eecs.berkeley.edu/~cs188/sp09/pacman.html

"""
In search.py, you will implement generic search algorithms which are called
by Pacman agents (in searchAgents.py).
"""

import util

class SearchProblem:
    """
    This class outlines the structure of a search problem, but doesn't implement
    any of the methods (in object-oriented terminology: an abstract class).

    You do not need to change anything in this class, ever.
    """

    def getStartState(self):
        """
        Returns the start state for the search problem
        """
        util.raiseNotDefined()

    def isGoalState(self, state):
        """
          state: Search state

        Returns True if and only if the state is a valid goal state
        """
        util.raiseNotDefined()

    def getSuccessors(self, state):
        """
          state: Search state

        For a given state, this should return a list of triples,
        (successor, action, stepCost), where 'successor' is a
        successor to the current state, 'action' is the action
        required to get there, and 'stepCost' is the incremental
        cost of expanding to that successor
        """
        util.raiseNotDefined()

    def getCostOfActions(self, actions):
        """
         actions: A list of actions to take

        This method returns the total cost of a particular sequence of actions.  The sequence must
        be composed of legal moves
        """
        util.raiseNotDefined()


def tinyMazeSearch(problem):
    """
    Returns a sequence of moves that solves tinyMaze.  For any other
    maze, the sequence of moves will be incorrect, so only use this for tinyMaze
    """
    from game import Directions
    s = Directions.SOUTH
    w = Directions.WEST
    return  [s,s,w,s,w,w,s,w]



def depthFirstSearch(problem):
    """
    Search the deepest nodes in the search tree first
    [2nd Edition: p 75, 3rd Edition: p 87]

    Your search algorithm needs to return a list of actions that reaches
    the goal.  Make sure to implement a graph search algorithm
    [2nd Edition: Fig. 3.18, 3rd Edition: Fig 3.7].

    To get started, you might want to try some of these simple commands to
    understand the search problem that is being passed in:

    print "Start:", problem.getStartState()
    print "Is the start a goal?", problem.isGoalState(problem.getStartState())
    print "Start's successors:", problem.getSuccessors(problem.getStartState())
    """
    "*** YOUR CODE HERE ***"

    Sons = util.Stack()

    start = problem.getStartState()

    Visited = []
    Visited.append(start)

    res = []
    
    first = (start, res)
    Sons.push(first)

    node = start
    Visited.append(node)

    while (not Sons.isEmpty() and not problem.isGoalState(node)):
        son = Sons.pop()
        node = son[0]
        res = son[1]
        Visited.append(node)
        successors = problem.getSuccessors(node)  
        for succ in successors:
            if (succ[0] not in Visited):
                node = succ[0]
                direct = succ[1]
                Sons.push((node,res+[direct]))

    return res+[direct]
    print problem.isGoalState(node)
    
    
    util.raiseNotDefined()

def breadthFirstSearch(problem):
    """
    Search the shallowest nodes in the search tree first.
    [2nd Edition: p 73, 3rd Edition: p 82]
    """
    "*** YOUR CODE HERE ***"
    Sons = util.Queue()

    temp = util.Queue()

    start = problem.getStartState()

    Visited = []
    Visited.append(start)

    res = []
    
    first = (start, res)
    Sons.push(first)
    temp.push(start)

    node = start
    Visited.append(node)


    while (not Sons.isEmpty()):
        son = Sons.pop()
        node = son[0]
        res = son[1]
        if problem.isGoalState(node):
            return res
        Visited.append(node)
        successors = problem.getSuccessors(node)  
        for succ in successors:
            if (succ[0] not in Visited):
                node = succ[0]
                direct = succ[1]
                if (node not in temp.list):
                    temp.push(node)
                    Sons.push((node,res+[direct]))

    return res+[direct]
    util.raiseNotDefined()

def uniformCostSearch(problem):
    "Search the node of least total cost first. "
    "*** YOUR CODE HERE ***"
    Sons = util.PriorityQueue()

    temp = util.Queue()
    store = util.Queue()

    start = problem.getStartState()

    Visited = []
    Visited.append(start)

    res = []
    priority = 0
    min = 0
    
    first = (start, res, priority) 
    Sons.push(first,priority)
    temp.push(start)

    node = start
    Visited.append(node)

    while (not Sons.isEmpty() and not problem.isGoalState(node)):
        son = Sons.pop()
        node = son[0]
        res = son[1]
        Visited.append(node)
        priority=son[2]
        successors = problem.getSuccessors(node)  
        for succ in successors:
            mincost = priority
            if (succ[0] not in Visited):
                node = succ[0]
                direct = succ[1]
                cost = succ[2]
                if (node not in temp.list):
                    temp.push(node)
                    mincost += cost
                    Sons.push((node, res+[direct],mincost),mincost)

    return res+[direct]
    util.raiseNotDefined()

def nullHeuristic(state, problem=None):
    """
    A heuristic function estimates the cost from the current state to the nearest
    goal in the provided SearchProblem.  This heuristic is trivial.
    """
    return 0

def aStarSearch(problem, heuristic=nullHeuristic):
    "Search the node that has the lowest combined cost and heuristic first."
    "*** YOUR CODE HERE ***"
    Sons = util.PriorityQueue()

    temp = util.Queue()
    store = util.Queue()

    start = problem.getStartState()

    Visited = []
    Visited.append(start)

    res = []
    cost = 0
    priority = cost + heuristic(start,problem)
    min = 0

    first = (start, res, cost) 
    Sons.push(first,priority)
    temp.push(start)

    node = start
    Visited.append(node)

    while (not Sons.isEmpty() and not problem.isGoalState(node)):
        son = Sons.pop()
        node = son[0]
        res = son[1]
        Visited.append(node)
        preCost=son[2]
        successors = problem.getSuccessors(node)  
        for succ in successors:
            mincost = preCost
            if (succ[0] not in Visited):
                node = succ[0]
                direct = succ[1]
                cost = succ[2]
                if (node not in temp.list):
                    temp.push(node)
                    mincost += cost 
                    Sons.push((node, res+[direct],mincost),mincost+heuristic(node,problem))

    return res+[direct]
    
    util.raiseNotDefined()


# Abbreviations
bfs = breadthFirstSearch
dfs = depthFirstSearch
astar = aStarSearch
ucs = uniformCostSearch