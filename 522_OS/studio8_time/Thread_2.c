
#include <linux/init.h>
#include <linux/module.h>
#include <linux/ktime.h>
#include <linux/kernel.h>

ktime_t time[2];

static int simple_init(void)
{
	memset(time,0,sizeof(time));
    printk(KERN_ALERT "module initialized\n");
    return 0;
}

static void simple_exit(void)
{
	int i;
	for (i=0;i<2;i++)
		printk(KERN_ALERT "timestamp %d is %lld\n", i, time[i]);
    printk(KERN_ALERT "module is unloaded\n");
}

module_init(simple_init);
module_exit(simple_exit);

MODULE_LICENSE ("GPL");
MODULE_AUTHOR ("WHY");
MODULE_DESCRIPTION ("studio8 q2");