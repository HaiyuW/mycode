#include <linux/init.h>
#include <linux/module.h>
#include <linux/ktime.h>
#include <linux/kernel.h>
#include <linux/sched.h>
#include <linux/kthread.h>
#include <uapi/linux/sched/types.h>

ktime_t time[3];
static struct task_struct * MyThread = NULL;

static int MyPrint(void *data)
{
        printk("MyPrint\n");
        return 0;
}

static int thread_init(void)
{
        struct sched_param param;
        param.sched_priority = 90;
        memset(time,0,sizeof(time));
        time[0] = ktime_get();
	MyThread = kthread_create(MyPrint,NULL,"test");
        time[1] = ktime_get();
        sched_setscheduler(MyThread, SCHED_FIFO, &param);
        time[2] = ktime_get();
        printk(KERN_ALERT "module initialized\n");
        return 0;
}

static void thread_exit(void)
{
        printk(KERN_ALERT "timestamp %d is %lld\n", 0, time[0]);
        printk(KERN_ALERT "timestamp %d is %lld\n", 1, time[1]);
        printk(KERN_ALERT "timestamp %d is %lld\n", 2, time[2]);
        printk(KERN_ALERT "time between 0 and 1 is %lld\n", time[1]-time[0]);
        printk(KERN_ALERT "time between 2 and 1 is %lld\n", time[2]-time[1]);
        printk(KERN_ALERT "module is unloaded\n");
}

module_init(thread_init);
module_exit(thread_exit);

MODULE_LICENSE("GPL");
MODULE_AUTHOR("WHY");
MODULE_DESCRIPTION("Studio8 Q3");