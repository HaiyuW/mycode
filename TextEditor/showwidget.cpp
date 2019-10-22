#include "showwidget.h"
#include <QHBoxLayout>

ShowWidget::ShowWidget(QWidget *parent) : QWidget(parent)
{
    imageLabel = new QLabel;
    text = new QTextEdit;
    QHBoxLayout *mainLayout = new QHBoxLayout(this);    //水平布局
    mainLayout->addWidget(text);
}
