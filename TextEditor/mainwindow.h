#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include <QImage>
#include <QLabel>
#include <QMenu>
#include <QMenuBar>
#include <QAction>
#include <QActionGroup>
#include <QAction>
#include "showwidget.h"
#include <QToolBar>
#include <QToolButton>
#include <QFontComboBox>
#include <QFont>
#include <QTextEdit>
#include <QUndoCommand>
#include <QTextLayout>
#include <QColor>
#include <QColorDialog>

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    explicit MainWindow(QWidget *parent = 0);
    ~MainWindow();

    void createlabels();
    void createcombobox();
    void createActions();   //创建动作
    void createMenus();     //创建菜单
    void createToolBars();
    void createButtons();

    void loadFile(QString filename);    //用于打开文件时载入文件
    void saveAFile(QString filename);   //用于保存文件
//  void keyPressEvent(QKeyEvent *event);    //用于确定键盘操作

private:
    QMenu *fileMenu;    //各项菜单栏
    QMenu *editMenu;

    QToolButton *underlineBtn;      //各项按钮
    QToolButton *boldBtn;
    QToolButton *italicBtn;
    QToolButton *colorBtn;

/**/QToolButton *alignleftBtn;
/**/QToolButton *aligncenterBtn;
/**/QToolButton *alignrightBtn;
/**/QToolButton *alignjustifyBtn;

    QLabel *fontLabel;              //字体字号
    QFontComboBox *fontComboBox;
    QLabel *fontLabel2;
    QComboBox *sizeComboBox;

    QComboBox *heightComboBox;//行距下拉栏

/**/QComboBox *liststyleComboBox;   //编号符号样式表

    QToolBar *fontToolBar;      //字体工具栏
/**/QToolBar *AlignToolBar;     //段落工具栏

    int exist;              //判断文本是否已经保存于本地文件中，以确定保存时是否需要确定保存路径
    QString fileName;   //文件名

    QUndoStack *m_stack;     //用于保存操作

    ShowWidget *showWidget;     //用于保存文本
    QString oldtext,newtext;

    QAction *openFileAction;    //文件菜单项
    QAction *newFileAction;
    QAction *saveAction;
    QAction *saveAsAction;
    QAction *saveAsHTMLAction;
    QAction *exitAction;
    QAction *encryptAction;
    QAction *decryptAction;

    QAction *undoAction;    //编辑菜单项
    QAction *redoAction;
    QAction *copyAction;
    QAction *cutAction;
    QAction *pasteAction;
    QAction *selectallAction;

protected slots:
    void showNewFile();     //新建文件槽函数
    void showOpenFile();    //打开文件槽函数
    void saveFile();        //保存文件槽函数
    void saveAsFile();      //文件另存为槽函数
    void saveAsHTMLFile();
    void encrypt();
    void decrypt();
    void m_undo();            //撤销槽函数
    void m_redo();            //返回槽函数
    void ShowUnderlineBtn();//下划线
    void ShowBoldBtn();     //加粗
    void ShowItalicBtn();   //斜体
/**/void Showalignleft();   //左对齐
/**/void Showaligncenter(); //居中
/**/void Showalignright();  //右对齐
/**/void Showalignjustify();//两端对齐
/**/void CheckPageChange(); //分页
    void ShowFontComboBox(QString comboStr);    //显示字体选择栏
    void ShowSizeSpinBox(QString spinValue);    //显示字体大小选择栏
    void mergeFormat(QTextCharFormat);          //合并格式
    void ShowCurrentFormatChanged(const QTextCharFormat &fmt);      //将选择的格式应用到文本上去
    void ShowColorBtn();
    void keyPressEvent();    //用于确定键盘操作
};

#endif // MAINWINDOW_H
