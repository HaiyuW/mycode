#ifndef M_INSERT_H
#define M_INSERT_H

#include <QUndoCommand>
#include <QTextEdit>

class m_insert : public QUndoCommand
{
    public:
        m_insert(QTextEdit *text, QString oldtext, QString newtext, QUndoCommand *parent):QUndoCommand(parent)
        {
            this->text = text;
            this->oldtext = oldtext;
            this->newtext = newtext;
        }
        void redo()
        {
        //    text->setText(newtext);
        }
        void undo()
        {
            text->setText(oldtext);
        }
    private:
        QTextEdit *text;
        QString oldtext;
        QString newtext;
};
#endif // M_INSERT_H
