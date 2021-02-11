# valueIterationAgents.py
# -----------------------
# Licensing Information: Please do not distribute or publish solutions to this
# project. You are free to use and extend these projects for educational
# purposes. The Pacman AI projects were developed at UC Berkeley, primarily by
# John DeNero (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# For more info, see http://inst.eecs.berkeley.edu/~cs188/sp09/pacman.html

import mdp, util

from learningAgents import ValueEstimationAgent

class ValueIterationAgent(ValueEstimationAgent):
    """
        * Please read learningAgents.py before reading this.*

        A ValueIterationAgent takes a Markov decision process
        (see mdp.py) on initialization and runs value iteration
        for a given number of iterations using the supplied
        discount factor.
    """
    def __init__(self, mdp, discount = 0.9, iterations = 100):
        """
          Your value iteration agent should take an mdp on
          construction, run the indicated number of iterations
          and then act according to the resulting policy.

          Some useful mdp methods you will use:
              mdp.getStates()
              mdp.getPossibleActions(state)
              mdp.getTransitionStatesAndProbs(state, action)
              mdp.getReward(state, action, nextState)
        """
        self.mdp = mdp
        self.discount = discount
        self.iterations = iterations
        self.values = util.Counter() # A Counter is a dict with default 0

        "*** YOUR CODE HERE ***"
        # get the states from mdp
        states = mdp.getStates()

        # iterate for 'iterations' times
        for i in range(iterations):
            # use a temp Counter to store the state and the value
            temp = util.Counter()
            for state in states:
                value = -100000
                # get max value along with the action and store in temp
                for action in mdp.getPossibleActions(state):
                    qvalue = self.getQValue(state, action)
                    if qvalue > value:
                        value = qvalue

                if value != -100000:
                    temp[state] = value

            self.values = temp

    def getValue(self, state):
        """
          Return the value of the state (computed in __init__).
        """
        return self.values[state]


    def getQValue(self, state, action):
        """
          The q-value of the state action pair
          (after the indicated number of value iteration
          passes).  Note that value iteration does not
          necessarily create this quantity and you may have
          to derive it on the fly.
        """
        "*** YOUR CODE HERE ***"
        resValue = 0
        # get next state and prob pair from mdp
        nextStatepairs = self.mdp.getTransitionStatesAndProbs(state, action)

        for nextStatepair in nextStatepairs:
            nextState = nextStatepair[0]
            prob = nextStatepair[1]

            # calculate reward by mdp.getReward
            reward = self.mdp.getReward(state, action, nextState)

            # if is terminal state, return the reward directly
            # if not, update the value using Bellman Equation
            if self.mdp.isTerminal(nextState):
                return reward
            else:
                resValue = resValue + prob*(reward + self.discount*self.getValue(nextState))

        return resValue

        util.raiseNotDefined()

    def getPolicy(self, state):
        """
          The policy is the best action in the given state
          according to the values computed by value iteration.
          You may break ties any way you see fit.  Note that if
          there are no legal actions, which is the case at the
          terminal state, you should return None.
        """
        "*** YOUR CODE HERE ***"

        actions = self.mdp.getPossibleActions(state)
        resAction = None
        max = -100000

        # get the actions along with the max value
        for action in actions:
            qvalue = self.getQValue(state, action)
            if qvalue > max:
                resAction = action
                max = qvalue

        return resAction
        util.raiseNotDefined()

    def getAction(self, state):
        "Returns the policy at the state (no exploration)."
        return self.getPolicy(state)
  
