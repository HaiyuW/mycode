# analysis.py
# -----------
# Licensing Information: Please do not distribute or publish solutions to this
# project. You are free to use and extend these projects for educational
# purposes. The Pacman AI projects were developed at UC Berkeley, primarily by
# John DeNero (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# For more info, see http://inst.eecs.berkeley.edu/~cs188/sp09/pacman.html

######################
# ANALYSIS QUESTIONS #
######################

# Set the given parameters to obtain the specified policies through
# value iteration.

def question2():

  # If there is no noise, there is no possibility for an unintended direction
  # so the agent can always be directed to the places with higher rewards
  # which is crossing the bridge.

  answerDiscount = 0.9
  answerNoise = 0.0
  return answerDiscount, answerNoise

def question3a():

  # Since it should prefer the close exit, the discount should be small enough that
  # the agent can give up the distant exit, though it has larger reward
  # If the living reward is larger, it's better to end the game as quickly as possible,
  # so it will risk the cliff to go to the exit point

  answerDiscount = 0.2
  answerNoise = 0.0
  answerLivingReward = 0.3
  return answerDiscount, answerNoise, answerLivingReward
  # If not possible, return 'NOT POSSIBLE'

def question3b():

  # The discount should be small, so it will prefer the close exit point
  # The reward should be 0, so the agent will choose the longer ways to go to the exit point
  # The noise helps the agent to choose unintended direction so that agent can have the
  # the ability to choose the ways which seems not optimal.

  answerDiscount = 0.2
  answerNoise = 0.2
  answerLivingReward = 0.0
  return answerDiscount, answerNoise, answerLivingReward
  # If not possible, return 'NOT POSSIBLE'

def question3c():

  # The discount should be large, so the further and higher exit point will be prefered,
  # since it has larger rewards.
  # If the living reward is larger, it's better to end the game as quickly as possible,
  # so it will risk the cliff to go to the exit point

  answerDiscount = 0.9
  answerNoise = 0.0
  answerLivingReward = 0.3
  return answerDiscount, answerNoise, answerLivingReward
  # If not possible, return 'NOT POSSIBLE'

def question3d():

  # The discount should be large, so the further and higher exit point will be prefered,
  # since it has larger rewards.
  # The reward should be 0, so the agent will choose the longer ways to go to the exit point
  # The noise helps the agent to choose unintended direction so that agent can have the
  # the ability to choose the ways which seems not optimal.

  answerDiscount = 0.9
  answerNoise = 0.2
  answerLivingReward = 0.0
  return answerDiscount, answerNoise, answerLivingReward
  # If not possible, return 'NOT POSSIBLE'

def question3e():

  # The discount should be 1, so the agent will prefer to exit at the distant exit point
  # The living reward should be large enough to make the agent not quit at the exit point
  # which means that the rewards on the path are larger than those at the exit point, so
  # agent will choose not to exit.

  answerDiscount = 1.0
  answerNoise = 0.0
  answerLivingReward = 0.3
  return answerDiscount, answerNoise, answerLivingReward
  # If not possible, return 'NOT POSSIBLE'

def question6():

  # When epsilon is 1, it can't find the optimal path, it will roll back to the start point
  # and then exit.
  # When epsilon is 0, no matter what learning rate is, agent will finally switch between the
  # start point and one-step right place. It never reaches another end.
  # Thus, I think it's impossible.

  answerEpsilon = None
  answerLearningRate = None
  return 'NOT POSSIBLE'
  # return answerEpsilon, answerLearningRate
  # If not possible, return 'NOT POSSIBLE'
  
if __name__ == '__main__':
  print 'Answers to analysis questions:'
  import analysis
  for q in [q for q in dir(analysis) if q.startswith('question')]:
    response = getattr(analysis, q)()
    print '  Question %s:\t%s' % (q, str(response))
