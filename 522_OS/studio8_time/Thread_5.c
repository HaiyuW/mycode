#include <linux/init.h>
#include <linux/module.h>
#include <linux/ktime.h>
#include <linux/kernel.h>
#include <linux/sched.h>
#include <linux/kthread.h>
#include <uapi/linux/sched/types.h>
#include <linux/timer.h>
#include <linux/jiffies.h>
#include <linux/types.h>
#include <linux/atomic.h>

#define THREAD_NUM 3

static struct task_struct * MyThread[THREAD_NUM];
static  atomic_t bar;
static int i;

static void bar_set(int n)
{
    atomic_set(&bar,n);
}

static void bar_wait(void)
{
    atomic_sub_return(1,&bar);
    while ((atomic_read(&bar))>0);
    return;
    
}

static int MyPrint(void *data)
{
        bar_wait();
        printk("MyPrint\n");
        set_current_state(TASK_INTERRUPTIBLE);
        return 0;
}

static int thread_init(void)
{
        bar_set(THREAD_NUM);
        for (i=0;i<THREAD_NUM;i++)
        {
            MyThread[i] = kthread_create(MyPrint,NULL,"test");
        }
        for (i=0;i<THREAD_NUM;i++)
            wake_up_process(MyThread[i]);
        printk(KERN_ALERT "module initialized\n");
        return 0;
}

static void thread_exit(void)
{
        printk(KERN_ALERT "module is unloaded\n");
}

module_init(thread_init);
module_exit(thread_exit);

MODULE_LICENSE("GPL");
MODULE_AUTHOR("WHY");
MODULE_DESCRIPTION("Studio8 Q5");