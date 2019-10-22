#include <linux/init.h>
#include <linux/module.h>
#include <linux/ktime.h>
#include <linux/kernel.h>
#include <linux/sched.h>
#include <linux/kthread.h>
#include <uapi/linux/sched/types.h>
#include <linux/timer.h>
#include <linux/jiffies.h>

ktime_t time[6];
static struct task_struct * MyThread = NULL;
static struct timer_list mytimer;

static int MyPrint(void *data)
{
        time[4] = ktime_get();
        printk(KERN_ALERT "MyPrint\n");
        set_current_state(TASK_INTERRUPTIBLE);
        time[5] = ktime_get();
        return 0;
}

static void timer_expire(struct timer_list *s)
{
        time[3] = ktime_get();
        wake_up_process(MyThread);
}

static int thread_init(void)
{
        struct sched_param param;
        param.sched_priority = 90;

        mytimer.expires = jiffies + HZ;
        mytimer.function = &timer_expire;

        memset(time,0,sizeof(time));
        time[0] = ktime_get();
	MyThread = kthread_create(MyPrint,NULL,"test");
        add_timer(&mytimer);
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
        printk(KERN_ALERT "timestamp %d is %lld\n", 3, time[3]);
        printk(KERN_ALERT "timestamp %d is %lld\n", 4, time[4]);
        printk(KERN_ALERT "timestamp %d is %lld\n", 5, time[5]);
        printk(KERN_ALERT "time between 0 and 4 is %lld\n", time[4]-time[0]);
        printk(KERN_ALERT "time between 3 and 5 is %lld\n", time[5]-time[3]);
        printk(KERN_ALERT "module is unloaded\n");
}

module_init(thread_init);
module_exit(thread_exit);

MODULE_LICENSE("GPL");
MODULE_AUTHOR("WHY");
MODULE_DESCRIPTION("Studio8 Q4");