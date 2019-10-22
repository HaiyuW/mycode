#include "mainwindow.h"
#include "showwidget.h"
#include <QApplication>
#include <QtCore>
#include <QFileDialog>
#include <QTextStream>
#include <QMessageBox>
#include <QUndoCommand>
#include <QKeyEvent>
#include "m_insert.h"
#include "m_remove.h"
#include <QTextLayout>

MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent)
{
    setWindowTitle(QString::fromLocal8Bit("TextEditor"));

    showWidget = new ShowWidget(this);
    setCentralWidget(showWidget);


/**//*fontLabel = new QLabel(QString::fromLocal8Bit("字体"));
    fontComboBox = new QFontComboBox;
    fontComboBox->setFontFilters(QFontComboBox::ScalableFonts);
    connect(fontComboBox,SIGNAL(activated(QString)),this,SLOT(ShowFontComboBox(Qstring)));*/

    connect(showWidget->text,SIGNAL(currentCharFormatChanged(QTextCharFormat)),
    this,SLOT(ShowCurrentFormatChanged(QTextCharFormat)));

/**/connect(showWidget->text,SIGNAL(textChanged()),
    this,SLOT(CheckPageChange()));
    connect(showWidget->text,SIGNAL(cursorPositionChanged()),
    this,SLOT(keyPressEvent()));
/**//*fontLabel2 = new QLabel(QString::fromLocal8Bit("字号"));
    sizeComboBox = new QComboBox;
    QFontDatabase db;
    foreach (int size,db.standardSizes())
    sizeComboBox->addItem(QString::number(size));
    connect(sizeComboBox,SIGNAL(activated(QString)),this,SLOT(ShowSizeSpinBox(QString)));*/

    colorBtn = new QToolButton;
    colorBtn->setText(QString::fromLocal8Bit("颜色"));
    colorBtn->setCheckable(true);
    connect(colorBtn,SIGNAL(clicked(bool)),this,SLOT(ShowColorBtn()));

    m_stack = new QUndoStack(this);

    this->exist = 0;

/**/createlabels();
/**/createcombobox();
    createActions();
    createMenus();
    createButtons();
    createToolBars();
}

/**/void MainWindow::createlabels(){
    fontLabel = new QLabel(QString::fromLocal8Bit("字体"));
    fontLabel2 = new QLabel(QString::fromLocal8Bit("字号"));
}

/**/void MainWindow::createcombobox(){
    fontComboBox = new QFontComboBox;
    fontComboBox->setFontFilters(QFontComboBox::ScalableFonts);
    connect(fontComboBox,SIGNAL(activated(QString)),this,SLOT(ShowFontComboBox(QString)));

    sizeComboBox = new QComboBox;
    QFontDatabase db;
    foreach (int size,db.standardSizes())
    sizeComboBox->addItem(QString::number(size));
    connect(sizeComboBox,SIGNAL(activated(QString)),this,SLOT(ShowSizeSpinBox(QString)));
}

void MainWindow::createButtons(){
    boldBtn = new QToolButton;
    boldBtn->setText(QString::fromLocal8Bit("粗体"));
    boldBtn->setCheckable(true);
    connect(boldBtn,SIGNAL(clicked(bool)),this,SLOT(ShowBoldBtn()));

    underlineBtn = new QToolButton;
    underlineBtn->setText(QString::fromLocal8Bit("下划线"));
    underlineBtn->setCheckable(true);
    connect(underlineBtn,SIGNAL(clicked(bool)),this,SLOT(ShowUnderlineBtn()));

    italicBtn = new QToolButton;
    italicBtn->setText(QString::fromLocal8Bit("斜体"));
    italicBtn->setCheckable(true);
    connect(italicBtn,SIGNAL(clicked(bool)),this,SLOT(ShowItalicBtn()));

    alignleftBtn = new QToolButton;
    alignleftBtn->setText(QString::fromLocal8Bit("左对齐"));
    alignleftBtn->setCheckable(false);
    connect(alignleftBtn,SIGNAL(clicked(bool)),this,SLOT(Showalignleft()));

    alignrightBtn = new QToolButton;
    alignrightBtn->setText(QString::fromLocal8Bit("右对齐"));
    alignrightBtn->setCheckable(false);
    connect(alignrightBtn,SIGNAL(clicked(bool)),this,SLOT(Showalignright()));

    aligncenterBtn = new QToolButton;
    aligncenterBtn->setText(QString::fromLocal8Bit("居中"));
    aligncenterBtn->setCheckable(false);
    connect(aligncenterBtn,SIGNAL(clicked(bool)),this,SLOT(Showaligncenter()));

    alignjustifyBtn = new QToolButton;
    alignjustifyBtn->setText(QString::fromLocal8Bit("两端对齐"));
    alignjustifyBtn->setCheckable(false);
    connect(alignjustifyBtn,SIGNAL(clicked(bool)),this,SLOT(Showalignjustify()));
}

void MainWindow::createToolBars(){
    //字体工具栏
    fontToolBar = addToolBar(tr("Font"));
    fontToolBar->addWidget(fontLabel);
    fontToolBar->addWidget(fontComboBox);
    fontToolBar->addWidget(fontLabel2);
    fontToolBar->addWidget(sizeComboBox);
    fontToolBar->addWidget(colorBtn);
    fontToolBar->addWidget(boldBtn);
    fontToolBar->addWidget(italicBtn);
    fontToolBar->addWidget(underlineBtn);

/**///段落工具栏
    AlignToolBar = addToolBar(tr("Font"));
    AlignToolBar->addWidget(alignleftBtn);
    AlignToolBar->addWidget(aligncenterBtn);
    AlignToolBar->addWidget(alignrightBtn);
    AlignToolBar->addWidget(alignjustifyBtn);
}

void MainWindow::createActions()
{
       //打开
       openFileAction = new QAction(QString::fromLocal8Bit("打开"),this);
       openFileAction->setShortcut(tr("Ctrl+O"));
       connect(openFileAction,SIGNAL(triggered(bool)),this,SLOT(showOpenFile()));

       //新建
       newFileAction = new QAction(QString::fromLocal8Bit("新建"),this );
       newFileAction->setShortcut(tr("Ctrl+N"));
       connect(newFileAction,SIGNAL(triggered(bool)),this,SLOT(showNewFile()));

       //保存
       saveAction = new QAction(QString::fromLocal8Bit("保存"),this );
       saveAction->setShortcut(tr("Ctrl+S"));
       connect(saveAction,SIGNAL(triggered(bool)),this,SLOT(saveFile()));

       //另存为
       saveAsAction = new QAction(QString::fromLocal8Bit("另存为"),this );
       connect(saveAsAction,SIGNAL(triggered(bool)),this,SLOT(saveAsFile()));

       //另存为HTML格式
       saveAsHTMLAction = new QAction(QString::fromLocal8Bit("另存为HTML"),this );
       connect(saveAsHTMLAction,SIGNAL(triggered(bool)),this,SLOT(saveAsHTMLFile()));

       //撤销
       undoAction = new QAction(QString::fromLocal8Bit("撤销"), this);
       undoAction->setShortcut(tr("Ctrl+Z"));
       connect(undoAction,SIGNAL(triggered(bool)),this,SLOT(m_undo()));

       //返回
       redoAction = new QAction(QString::fromLocal8Bit("返回"), this);
       connect(redoAction,SIGNAL(triggered(bool)),this,SLOT(m_redo()));

       //通过使用Qt自带的close、copy、cut、paste、selectAll控件实现
       //退出
       exitAction = new QAction(QString::fromLocal8Bit("退出"), this);
       connect(exitAction,SIGNAL(triggered(bool)),this,SLOT(close()));

       //加密
       encryptAction = new QAction(QString::fromLocal8Bit("加密"),this);
       connect(encryptAction,SIGNAL(triggered(bool)),this,SLOT(encrypt()));

       //解密
       decryptAction = new QAction(QString::fromLocal8Bit("解密"),this);
       connect(decryptAction,SIGNAL(triggered(bool)),this,SLOT(decrypt()));

       //复制
       copyAction = new QAction(QString::fromLocal8Bit("复制") , this);
       copyAction->setShortcut(tr("Ctrl+C"));
       connect(copyAction,SIGNAL(triggered(bool)),showWidget->text,SLOT(copy()));

       //剪切
       cutAction = new QAction(QString::fromLocal8Bit("剪切") ,this);
       cutAction->setShortcut(tr("Ctrl+X"));
       connect(cutAction,SIGNAL(triggered(bool)),showWidget->text,SLOT(cut()));

       //粘贴
       pasteAction = new QAction(QString::fromLocal8Bit("粘贴") ,this);
       pasteAction->setShortcut(tr("Ctrl+P"));
       connect(pasteAction,SIGNAL(triggered(bool)),showWidget->text,SLOT(paste()));

       //全选
       selectallAction = new QAction(QString::fromLocal8Bit("全选"),this);
       selectallAction->setShortcut(tr("Ctrl+A"));
       connect(selectallAction,SIGNAL(triggered(bool)),showWidget->text,SLOT(selectAll()));


}

void MainWindow::createMenus()  //创建菜单栏
{
    fileMenu = menuBar()->addMenu(QString::fromLocal8Bit("文件"));
    fileMenu->addAction(newFileAction);
    fileMenu->addAction(openFileAction);
    fileMenu->addAction(saveAction);
    fileMenu->addAction(saveAsAction);
    fileMenu->addAction(saveAsHTMLAction);
    fileMenu->addAction(exitAction);
    fileMenu->addAction(encryptAction);
    fileMenu->addAction(decryptAction);

    editMenu = menuBar()->addMenu(QString::fromLocal8Bit("编辑"));
    editMenu->addAction(undoAction);
    editMenu->addAction(redoAction);
    editMenu->addAction(copyAction);
    editMenu->addAction(cutAction);
    editMenu->addAction(pasteAction);
    editMenu->addAction(selectallAction);
}

void MainWindow::showNewFile()  //新建文件
{
     MainWindow *newMainWindow = new MainWindow;
     newMainWindow->exist = 0;
     newMainWindow->show();
}

void MainWindow::showOpenFile() //打开文件
{
    fileName = QFileDialog::getOpenFileName(this);
    if(!fileName.isEmpty())
    {
        MainWindow *newMainWindow = new MainWindow;
        newMainWindow->exist = 1;
        newMainWindow->fileName = fileName;
        newMainWindow->show();
        newMainWindow->loadFile(fileName);
    }
    else
    {
        QMessageBox::warning(this, tr("Error"), QString::fromLocal8Bit("打开文件时出现错误"));
    }
}

void MainWindow::loadFile(QString filename) //载入文件
{
    QFile file(filename);
    if(file.open(QIODevice::ReadOnly | QIODevice::Text))
    {
        QTextStream textStream(&file);
        while(!textStream.atEnd())
        {
            showWidget->text->append(textStream.readLine());
        }
    }
    else
    {
        QMessageBox::warning(this, tr("Error"), QString::fromLocal8Bit("载入文件时出现错误"));
    }
}

void MainWindow::saveFile() //保存文件
{
    if(this->exist == 0)
    {
        saveAsFile();
    }
    else
    {
        saveAFile(fileName);
    }
}

void MainWindow::saveAsFile() //文件另存为
{
    QString filename = QFileDialog::getSaveFileName(this, QString::fromLocal8Bit("另存为"), "", tr("Config Files(.txt)"));
    if(!filename.isEmpty())
    {
        saveAFile(filename);
    }
    else
    {
        QMessageBox::warning(this, tr("Error"), QString::fromLocal8Bit("；另存为时出现错误"));
    }
}

void MainWindow::saveAsHTMLFile()
{
    QString filename = QFileDialog::getSaveFileName(this, QString::fromLocal8Bit("另存为"), "", tr("Config Files(.html)"));
    QFile file(filename);
    if(file.open(QIODevice::WriteOnly | QIODevice::Text))
    {
        QTextStream out(&file);
        out << showWidget->text->toHtml();
        exist = 1;
        fileName = filename;
    }
    else
    {
        QMessageBox::warning(this, tr("Error"), QString::fromLocal8Bit("保存文件时出现错误"));
    }
}

void MainWindow::saveAFile(QString filename) //保存文件
{
    QFile file(filename);
    if(file.open(QIODevice::WriteOnly | QIODevice::Text))
    {
        QTextStream out(&file);
        out << showWidget->text->toPlainText();
        exist = 1;
        fileName = filename;
    }
    else
    {
        QMessageBox::warning(this, tr("Error"), QString::fromLocal8Bit("保存文件时出现错误"));
    }
}
void MainWindow::encrypt()
{
    //QMessageBox::information(this, QString::fromLocal8Bit("警告"),this->fileName);
    //if (this->fileName == "")
    //       QMessageBox::information(this,QString::fromLocal8Bit("警告"),QString::fromLocal8Bit("傻子!"));
    //else AESenc
    QFile file(fileName);
    QTextStream in(&file);
    QString str;
    if(file.open(QIODevice::ReadWrite))
    {
        str = in.readAll();
        qDebug() << str;
        int len = str.length();
        for(int i=0;i<len;++i)
        {
            str[i] = QChar::fromLatin1((str[i].toLatin1()+64) % 128);
            qDebug() << str;
        }

    }
    file.close();

    QTextStream out(&file);
    file.open(QIODevice::WriteOnly);
    out << str;
    file.close();
}
void MainWindow::decrypt()
{
    //QMessageBox::information(this, QString::fromLocal8Bit("警告"),this->fileName);
    //if (this->fileName == "")
    //       QMessageBox::information(this,QString::fromLocal8Bit("警告"),QString::fromLocal8Bit("傻子!"));
    //else AESenc
    QFile file(fileName);
    QTextStream in(&file);
    QString str;
    if(file.open(QIODevice::ReadWrite))
    {
        str = in.readAll();
        qDebug() << str;
        int len = str.length();
        for(int i=0;i<len;++i)
        {
            str[i] = QChar::fromLatin1((str[i].toLatin1()+64) % 128);
            qDebug() << str;
        }

    }
    file.close();

    QTextStream out(&file);
    file.open(QIODevice::WriteOnly);
    out << str;
    file.close();
}
/*
void MainWindow::keyPressEvent(QKeyEvent *event)
{
    QString chars = event->text();
    QTextCursor cursor = showWidget->text->textCursor();
    int index = cursor.position();
    switch (event->key())
    {
        case Qt::Key_Backspace:
            if (index > 0)	m_stack->push(new m_remove(showWidget->text, index-1, 1);
            break;
        case Qt::Key_Delete:
            if (index < showWidget->text->toPlainText().length())	m_stack->push(new m_remove(showWidget->text, index, 1));
            break;
        default:
            if (!chars.isEmpty())	m_stack->push(new m_insert(showWidget->text, index, chars));
            break;
    }
}
*/

void MainWindow::keyPressEvent()
{
    if(showWidget->text->toPlainText() != oldtext)
    {
       newtext = showWidget->text->toPlainText();
//       qDebug()<<newtext<<endl<<oldtext<<endl;
       m_stack->push(new m_insert(showWidget->text,oldtext,newtext,NULL));
//       qDebug()<<"ok"<<endl;
       oldtext = newtext;
    }
}


//undo执行时光标会回到文档首，redo实现时有bug，调用qt控件
void MainWindow::m_undo()
{
//    m_stack->undo();
    showWidget->text->undo();
}

void MainWindow::m_redo()
{
//    m_stack->redo();
    showWidget->text->redo();
}

//以下为字体相关函数
void MainWindow::ShowUnderlineBtn(){
    QTextCharFormat fmt;
    fmt.setFontUnderline(underlineBtn->isChecked());
    showWidget->text->mergeCurrentCharFormat(fmt);
}

void MainWindow::ShowBoldBtn(){
    QTextCharFormat fmt;
    fmt.setFontWeight(boldBtn->isChecked()?QFont::Bold:QFont::Normal);
    showWidget->text->mergeCurrentCharFormat(fmt);
}

void MainWindow::ShowItalicBtn(){
    QTextCharFormat fmt;
    fmt.setFontItalic(italicBtn->isChecked());
    showWidget->text->mergeCurrentCharFormat(fmt);
}

void MainWindow::ShowFontComboBox(QString comboStr){
    QTextCharFormat fmt;
    fmt.setFontFamily(comboStr);
    mergeFormat(fmt);
}

/**/void MainWindow::Showalignleft(){
    showWidget->text->setAlignment(Qt::AlignLeft | Qt::AlignAbsolute);
}

/**/void MainWindow::Showalignright(){
    showWidget->text->setAlignment(Qt::AlignRight | Qt::AlignAbsolute);
}

/**/void MainWindow::Showaligncenter(){
    showWidget->text->setAlignment(Qt::AlignHCenter);
}

/**/void MainWindow::Showalignjustify(){
    showWidget->text->setAlignment(Qt::AlignJustify);
}

void MainWindow::ShowSizeSpinBox(QString spinValue){
    QTextCharFormat fmt;
    fmt.setFontPointSize(spinValue.toFloat());
    showWidget->text->mergeCurrentCharFormat(fmt);
}

void MainWindow::mergeFormat(QTextCharFormat format){
    QTextCursor cursor = showWidget->text->textCursor();
    if (!cursor.hasSelection())
        cursor.select(QTextCursor::WordUnderCursor);
    cursor.mergeCharFormat(format);
    showWidget->text->mergeCurrentCharFormat(format);
}

void MainWindow::ShowColorBtn(){
    QColor color = QColorDialog::getColor(Qt::red,this);
    if (color.isValid())
    {
        QTextCharFormat fmt;
        fmt.setForeground(color);
        showWidget->text->mergeCurrentCharFormat(fmt);
    }
}

/**/void MainWindow::CheckPageChange(){
    QTextCursor tc = showWidget->text->textCursor();
    static int page=0;
    int blocknumbers = showWidget->text->document()->blockCount();
    QString pageinfo;
    QString changechar;
    if(blocknumbers>page*11+10)
    {
        page+=1;
        pageinfo="*****↑page1*****page2↓*****\n";
        changechar.setNum(page+1);
        pageinfo.replace(20, 1, changechar);
        changechar.setNum(page);
        pageinfo.replace(10,1,changechar);
        tc.insertText(pageinfo);
    }
}

void MainWindow::ShowCurrentFormatChanged(const QTextCharFormat &fmt){
    fontComboBox->setCurrentIndex(fontComboBox->findText(fmt.fontFamily()));
    sizeComboBox->setCurrentIndex(sizeComboBox->findText(QString::number(fmt.fontPointSize())));
    boldBtn->setChecked(fmt.font().bold());
    italicBtn->setChecked(fmt.fontItalic());
    underlineBtn->setChecked(fmt.fontUnderline());
}

MainWindow::~MainWindow()
{

}
