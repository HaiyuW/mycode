# oceanTeam.py
# ---------------
# Licensing Information: Please do not distribute or publish solutions to this
# project. You are free to use and extend these projects for educational
# purposes. The Pacman AI projects were developed at UC Berkeley, primarily by
# John DeNero (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# For more info, see http://inst.eecs.berkeley.edu/~cs188/sp09/pacman.html

from captureAgents import CaptureAgent
import distanceCalculator
import random, time, util
from game import Directions
import game
from util import nearestPoint


#################
# Team creation #
#################

def createTeam(firstIndex, secondIndex, isRed,
               first='OffensiveAgent', second='DefensiveAgent'):
    """
    This function should return a list of two agents that will form the
    team, initialized using firstIndex and secondIndex as their agent
    index numbers.  isRed is True if the red team is being created, and
    will be False if the blue team is being created.

    As a potentially helpful development aid, this function can take
    additional string-valued keyword arguments ("first" and "second" are
    such arguments in the case of this function), which will come from
    the --redOpts and --blueOpts command-line arguments to capture.py.
    For the nightly contest, however, your team will be created without
    any extra arguments, so you should make sure that the default
    behavior is what you want for the nightly contest.
    """
    return [eval(first)(firstIndex), eval(second)(secondIndex)]


##########
# Agents #
##########


class OffensiveAgent(CaptureAgent):
    """
    offensive agent uses expectimax method. When agent offensives, it will reach
    to the evaluation function. The evaluation function is handled by the features
    and weights. Thus, it will choose maxValue and the average minValue and finally
    decides the best actions to perform
    """
    def registerInitialState(self, gameState):
        """
        This method handles the initial setup of the
        agent to populate useful fields (such as what team
        we're on).

        A distanceCalculator instance caches the maze distances
        between each pair of positions, so your agents can use:
        self.distancer.getDistance(p1, p2)
        """
        # CaptureAgent.registerInitialState(gameState)
        self.red = gameState.isOnRedTeam(self.index)
        self.distancer = distanceCalculator.Distancer(gameState.data.layout)

        # comment this out to forgo maze distance computation and use manhattan distances
        self.distancer.getMazeDistances()

        import __main__
        if '_display' in dir(__main__):
            self.display = __main__._display

        # record the offense status and target position
        self.offense = False
        self.targetPos = None

        # use these variables to decide the moving distances
        self.w = gameState.getWalls().width
        self.h = gameState.getWalls().height

    def chooseAction(self, gameState):
        """
          Returns the expectimax action from the current gameState using self.depth
          and self.evaluationFunction. In this case, we use the self.depth as 2 and
          evaluationFunction as the multiple of features and weights
        """
        beginTime = time.time()
        actions = gameState.getLegalActions(self.index)
        actions.remove(Directions.STOP)

        # if the offencer is eaten by the defencer, pick another way to invade
        if gameState.getAgentPosition(self.index) == gameState.getInitialAgentPosition(self.index):
            self.offense = False
        # if the state is pacman, which means it is an offencer then update the offence status
        if gameState.getAgentState(self.index).isPacman:
            self.offense = True
        # choose a target postion to offense and if it has a wall then pick another one
        elif self.offense:
            self.offense = False
            myPos = gameState.getAgentPosition(self.index)
            # print myPos
            self.targetPos = (myPos[0], random.choice(range(self.h)))
            while gameState.hasWall(self.targetPos[0], self.targetPos[1]):
                self.targetPos = (myPos[0], random.choice(range(self.h)))

        # traverse all the actions and form a action/targetDist dictionary
        # and then return the min dist action
        if self.targetPos:
            myPos = gameState.getAgentPosition(self.index)
            if myPos != self.targetPos:
                # don't cross the boarder
                for action in actions:
                    if gameState.generateSuccessor(self.index, action).getAgentState(self.index).isPacman:
                        actions.remove(action)
                actions_dist = {}
                for action in actions:
                    actions_dist[action] = self.getMazeDistance(myPos, self.targetPos)
                return min(actions_dist, key=actions_dist.get)
            else:
                self.targetPos = None

        # get the opponents
        opponents = {}
        for opponent in self.getOpponents(gameState):
            opponents[opponent] = gameState.getAgentState(opponent)

        self.ghosts = []

        for i, opponent in opponents.items():
            if not opponent.isPacman:
                self.ghosts.append(i)
        self.depth = 2

        # traverse the actions and check the minValue of each actions with depth 2
        # and then choose the max value
        # travese the action_val dictionary again to get the actions with max value
        actions_val = {}
        for action in actions:
            actions_val[action] = self.minValue(gameState.generateSuccessor(self.index, action), 0, 0)

        max_val = max(actions_val.values())

        best_actions = []
        for action, val in actions_val.items():
            if val == max_val:
                best_actions.append(action)
        endTime = time.time()-beginTime

        return random.choice(best_actions)

    # max value is simple, get the max value of the min value.
    def maxValue(self, gameState, level):
        if level == self.depth or gameState.isOver():
            return self.evaluationFunction(gameState)

        actions = gameState.getLegalActions(self.index)
        actions.remove(Directions.STOP)

        return max(self.minValue(gameState.generateSuccessor(self.index, action), 0, level) for action in actions)

    # min value is to get the average value of the next depth value
    def minValue(self, gameState, ghost_id, level):
        if level == self.depth or gameState.isOver():
            return self.evaluationFunction(gameState)

        # when the ghosts are invisible, we can simply reach out to the next depth and return the value
        ghostPos = gameState.getAgentPosition(self.ghosts[ghost_id])
        if not ghostPos or self.getMazeDistance(ghostPos, gameState.getAgentState(self.index).getPosition()) > 3:
            if ghost_id < len(self.ghosts) - 1:
                return self.minValue(gameState, ghost_id + 1, level)
            else:
                return self.maxValue(gameState, level + 1)
        # when the ghosts are visible, we need to traverse all the actions and return the avg value of each action
        else:
            actions = gameState.getLegalActions(self.ghosts[ghost_id])
            # if we haven't traversed all the ghosts, get the next ghosts
            if ghost_id < len(self.ghosts) - 1:
                minSum = sum(self.minValue(gameState.generateSuccessor(self.ghosts[ghost_id], action), ghost_id + 1, level) for action in actions)
                return minSum/len(actions)
            # else, get the next depth
            else:
                minSum = sum(self.maxValue(gameState.generateSuccessor(self.ghosts[ghost_id], action), level + 1) for action in actions)
                return minSum/len(actions)

    def evaluationFunction(self, gameState):
        features = self.getFeatures(gameState)
        weights = self.getWeights(gameState)
        return features * weights

    # the features contains:
    # 1) when successor can end the game, good
    # 2) when food is near, choose the nearest food, good
    # 3) when ghosts are near, get rid of the ghosts, good
    # 4) get caught, bad
    def getFeatures(self, gameState):
        features = util.Counter()
        successor = gameState
        if successor.isOver():
            features['succScore'] = 1000
            return features
        features['succScore'] = self.getScore(successor)
        myPos = successor.getAgentState(self.index).getPosition()
        if successor.getAgentState(self.index).isPacman:
            atHome = True
        else:
            atHome = False

        # Compute distance to the nearest food or capsule
        foodList = self.getFood(successor).asList()
        if len(foodList)>0:
            features['distToFood'] = min([self.getMazeDistance(myPos, food) for food in foodList])

        # be careful about the ghosts!
        for i in self.ghosts:
            # check if it's close
            dist = successor.getAgentPosition(i)
            if dist and dist < 3 and not atHome and not successor.getAgentState(i).scaredTimer:
                features['closeGhost' + str(i)] = 1000
                # get caught!
                if not dist:
                    features['closeGhost' + str(i)] = -1000

        return features

    def getWeights(self, gameState):
        w = {'succScore': 1000, 'distToFood': -10}
        for i in self.ghosts:
            w['closeGhost' + str(i)] = 10
        return w


class DefensiveAgent(CaptureAgent):
    """
     Defensive Agent, we use the buster agent in project 4. Particle filter can handle
     the particle and elapse time. This agent can detect the offencers' postion and then
     update the belief distribution. Thus, it can move toward to the offencers.
    """

    def registerInitialState(self, gameState):
        "Initial setups"
        # CaptureAgent.registerInitialState(gameState)
        self.red = gameState.isOnRedTeam(self.index)
        self.distancer = distanceCalculator.Distancer(gameState.data.layout)
        self.distancer.getMazeDistances()

        import __main__
        if '_display' in dir(__main__):
            self.display = __main__._display

        self.inferenceModules = None
        self.firstMove = True

        # these 2 variables are import to calculate the distances
        self.w = gameState.getWalls().width
        self.h = gameState.getWalls().height

    def getAction(self, gameState):
        # update beliefs based on the position of the offencers
        # then chooses an action based on updated beliefs.
        self.beginTime = time.time()
        self.observationHistory.append(gameState)
        myState = gameState.getAgentState(self.index)
        myPos = myState.getPosition()
        if myPos != nearestPoint(myPos):
            # We're halfway from one position to the next
            return gameState.getLegalActions(self.index)[0]

        opponents = []

        for opponent in self.getOpponents(gameState):
            opponents.append(gameState.getAgentState(opponent))

        self.invaders = []
        for opponent in opponents:
            if opponent.isPacman:
                self.invaders = []
        # when no offencers, move around the boarder to defense the offencers ways to get into the district
        if not self.invaders:
            self.inferenceModules = None
            return self.moveAtBoarder(gameState)
        # have offencers, use particle filter to track
        else:
            # if no inference, initialize the inference with the particle filter
            if not self.inferenceModules:
                self.inferenceModules = JointParticleFilter(gameState, self.index, self.invaders)
            # if not the first move, we need to consider the elapse time
            if not self.firstMove:
                self.inferenceModules.elapseTime(gameState)
            # update the first move status
            self.firstMove = False
            self.inferenceModules.observeState(gameState)
            self.ghostBeliefs = self.inferenceModules.getBeliefDistribution()
        return self.chooseAction(gameState)

    # choose actions based on ghosts position and status
    # if ghosts are not scared, move toward to the pacman
    # else, choose the actions with largest distance
    def chooseAction(self, gameState):
        pacmanPosition = gameState.getAgentState(self.index).getPosition()
        legal = gameState.getLegalActions(self.index)
        legal.remove(Directions.STOP)

        # don't cross the boarder
        for action in legal:
            if gameState.generateSuccessor(self.index, action).getAgentState(self.index).isPacman:
                legal.remove(action)

        ghostsPos = []
        for i in range(len(self.invaders)):
            # if ghosts can see pacmans, update the belief
            if self.invaders[i].getPosition():
                ghostsPos.append(self.invaders[i].getPosition())
                self.inferenceModules.updatePosition(self.invaders[i].getPosition(), i)
            # else, ghosts go to the postions with max belief
            else:
                ghostsPos.append(max(self.ghostBeliefs[i], key=self.ghostBeliefs[i].get))

        # get the closest ghost position
        ghostsDist = {}
        for pos in ghostsPos:
            ghostsDist[pos] = self.getMazeDistance(pacmanPosition, pos)

        closestGhostPos = min(ghostsDist, key=ghostsDist.get)

        # return the action that minimizes the distance if not scared
        # or maximize if scared
        closestGhostDist = {}
        for action in legal:
            closestGhostDist[action] = self.getMazeDistance(game.Actions.getSuccessor(pacmanPosition, action), closestGhostPos)

        if gameState.getAgentState(self.index).scaredTimer:
            maxValue = max(closestGhostDist.values())
            bestActions = []
            for action, val in closestGhostDist.items():
                if val == maxValue:
                    bestActions.append(action)
        else:
            minValue = min(closestGhostDist.values())
            bestActions = []
            for action, val in closestGhostDist.items():
                if val == minValue:
                    bestActions.append(action)

        endTime = time.time()-self.beginTime

        return random.choice(bestActions)

    # this funtion makes the ghosts move at the boarder when they don't see any pacman
    def moveAtBoarder(self, gameState):
        actions = gameState.getLegalActions(self.index)
        actions.remove(Directions.STOP)

        if self.red:
            loc = [(x, y) for x, y in zip([self.w / 2 - 2] * 3, range(self.h / 2 - 1, self.h / 2 + 2))]
        else:
            loc = [(x, y) for x, y in zip([self.w / 2 + 2] * 3, range(self.h / 2 - 1, self.h / 2 + 2))]

        x, y = random.choice(loc)
        while gameState.hasWall(x, y):
            x, y = random.choice(loc)
        pos = tuple([x, y])
        myPos = gameState.getAgentState(self.index).getPosition()

        distances = []
        for action in actions:
            distances.append(self.getMazeDistance(pos, game.Actions.getSuccessor(myPos, action)))

        minValue = min(distances)
        bestActions = [a for a, v in zip(actions, distances) if v == minValue]

        return random.choice(bestActions)


class JointParticleFilter:

    def __init__(self, gameState, index, invaders):
        self.legalPositions = [p for p in gameState.getWalls().asList(False)]
        self.walls = gameState.getWalls()
        self.invaders = invaders
        self.index = index
        self.numInvaders = len(invaders)
        self.numParticles = 100 * self.numInvaders

        self.ghostAgents = invaders
        self.particles = [tuple(random.choice(self.legalPositions) for _ in range(self.numInvaders)) for _ in
                          range(self.numParticles)]

     # if ghosts see the pacman, update the particles
    def updatePosition(self, pos, ghost_id):
        updatedParticles = []
        for particle in self.particles:
            part = list(particle)
            part[ghost_id] = pos
            updatedParticles.append(tuple(part))

        self.particles = updatedParticles

    # the same function in project 4 to handle elapse time
    def elapseTime(self, gameState):
        newParticles = []
        for oldParticle in self.particles:
            newParticle = list(oldParticle)
            newParticle = tuple(random.choice(game.Actions.getLegalNeighbors(pos, self.walls)) for pos in newParticle)
            newParticles.append(newParticle)
        self.particles = newParticles

    def observeState(self, gameState):
        pacmanPosition = gameState.getAgentState(self.index).getPosition()
        noisyDistances = gameState.getAgentDistances()

        weights = util.Counter()
        for p in self.particles:
            t = 1
            for i in range(self.numInvaders):
                dist = util.manhattanDistance(p[i], pacmanPosition)
                t *= gameState.getDistanceProb(dist, noisyDistances[i])
            weights[p] += t

        if not any(weights.values()):
            self.particles = [tuple(random.choice(self.legalPositions) for _ in range(self.numInvaders)) for _ in
                              range(self.numParticles)]
            return None

        weights.normalize()
        # resample
        self.particles = [util.sample(weights) for _ in range(self.numParticles)]

    # use particles and elapse time to update belief distribution
    def getBeliefDistribution(self):
        jointDistribution = util.Counter()
        for part in self.particles: jointDistribution[part] += 1
        jointDistribution.normalize()
        # marginalize the belief over each invader
        dist = []
        for ghostIdx in range(self.numInvaders):
            dist.append(util.Counter())
            for t, prob in jointDistribution.items():
                dist[-1][t[ghostIdx]] += prob
        return dist