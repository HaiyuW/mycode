import m5
from m5.objects import *

import os
gem5_path = os.environ["GEM5"]

import optparse
parser = optparse.OptionParser()
parser.add_option("--prog", type="str", default=None);
parser.add_option("--clock_freq", type="str", default=None);
parser.add_option("--l1i_size",type="str",default=None);
parser.add_option("--l1i_assoc",type="int",default=None);
parser.add_option("--l1d_size",type="str",default=None);
parser.add_option("--l1d_assoc",type="int",default=None);
(options, args) = parser.parse_args()
program = options.prog

system = System()

system.clk_domain = SrcClockDomain()

system.clk_domain.voltage_domain = VoltageDomain()

isa = m5.defines.buildEnv['TARGET_ISA']
# if isa == "x86":
# 	system.clk_domain.clock = '1GHz'
# elif isa == "arm":
# 	system.clk_domain.clock = '1.2GHz'

if options.clock_freq:
	system.clk_domain.clock = options.clock_freq
else:
	system.clk_domain.clock = '1.2GHz'

from Caches import *

system.mem_mode = 'timing'
system.mem_ranges = [AddrRange('512MB')]

system.cpu = TimingSimpleCPU()

system.membus = SystemXBar()

# system.cpu.icache_port = system.membus.slave
# system.cpu.dcache_port = system.membus.slave

system.cpu.icache = L1ICache(options)
system.cpu.dcache = L1DCache(options)

system.cpu.icache.connectCPU(system.cpu)
system.cpu.dcache.connectCPU(system.cpu)

system.l2bus = L2XBar()
system.cpu.icache.connectBus(system.l2bus)
system.cpu.dcache.connectBus(system.l2bus)

system.l2cache = L2Cache(options)
system.l2cache.connectCPUSideBus(system.l2bus)
system.l2cache.connectMemSideBus(system.membus)

system.cpu.createInterruptController()
if isa == 'x86':
	system.cpu.interrupts[0].pio = system.membus.master
	system.cpu.interrupts[0].int_master = system.membus.slave
	system.cpu.interrupts[0].int_slave = system.membus.master

system.system_port = system.membus.slave

system.mem_ctrl = DDR3_1600_8x8()
system.mem_ctrl.range = system.mem_ranges[0]
system.mem_ctrl.port = system.membus.master

process = Process()
apps_path = "/project/linuxlab/gem5/test_progs"
if program == "daxpy" and isa == "x86":
	process.cmd = [apps_path + '/daxpy/daxpy_x86']
elif program == "daxpy" and isa == "arm":
	process.cmd = [apps_path + '/daxpy/daxpy_arm']
elif program == "queens" and isa == "x86":
	process.cmd = [apps_path + '/queens/queens_x86']
	process.cmd += ["10 -c"]
elif program == "queens" and isa == "arm":
	process.cmd = [apps_path + '/queens/queens_arm']
	process.cmd += ["10 -c"]

system.cpu.workload = process
system.cpu.createThreads()

root = Root(full_system = False, system = system)
m5.instantiate()
print ("Beginning simulation!")

exit_event = m5.simulate()
print('Exiting @ tick %i because %s' % (m5.curTick(), exit_event.getCause()))


