from m5.defines import buildEnv
from m5.objects import *

class L1Cache(Cache):
    assoc = 2
    tag_latency = 2
    data_latency = 2
    response_latency = 2
    mshrs = 4
    tgts_per_mshr = 20

    def __init__(self, options=None):
        super(L1Cache,self).__init__()
        pass
    def connectBus(self,bus):
        self.mem_side = bus.slave
    def connectCPU(self,cpu):
        raise NotImplementedError

class L1ICache(L1Cache):
    is_read_only = True
    writeback_clean = True
    size = '16kB'

    def __init__(self,opts=None):
        super(L1ICache,self).__init__(opts)
        if opts.l1i_size:
            self.size = opts.l1i_size
        if opts.l1i_assoc:
            self.assoc = opts.l1i_assoc
    
    def connectCPU(self, cpu):
        self.cpu_side = cpu.icache_port

class L1DCache(L1Cache):
    is_read_only = False
    writeback_clean = False
    size = '64kB'

    def __init__(self,opts=None):
        super(L1DCache,self).__init__(opts)
        if opts.l1d_size:
            self.size = opts.l1d_size
        if opts.l1d_assoc:
            self.assoc = opts.l1d_assoc
    
    def connectCPU(self, cpu):
        self.cpu_side = cpu.dcache_port

class L2Cache(Cache):
    is_read_only = False
    writeback_clean = False
    size = '256kB'
    assoc = 8
    tag_latency = 20
    data_latency = 20
    response_latency = 80
    mshrs = 20
    tgts_per_mshr = 12

    def __init__(self,options=None):
        super(L2Cache,self).__init__()
        pass

    def connectCPUSideBus(self,bus):
        self.cpu_side = bus.master
    def connectMemSideBus(self,bus):
        self.mem_side = bus.slave
