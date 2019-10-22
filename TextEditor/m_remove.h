#ifndef M_REMOVE_H
#define M_REMOVE_H

#include <QUndoCommand>
#include <QTextEdit>

class m_remove : public QUndoCommand
{
    public:
        m_remove(QTextEdit *editor, int index, int count) : QUndoCommand("Remove characters")
        {
            m_index = index;
            m_count = count;
            m_editor  = editor;
        }
        virtual void redo()
        {
        //    m_removedchar = m_text.mid(m_index, m_count);
        //    m_text.remove(m_index, m_count);
        }
        virtual void undo()
        {

        //    m_text.insert(m_index, m_removedchar);
        }
    private:
        int m_index, m_count;
        QString m_removedchar;
        QTextEdit *m_editor;
};

#endif // M_REMOVE_H
