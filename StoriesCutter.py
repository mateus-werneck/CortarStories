import wx, time
from conversion import cutVideo
from threading import *

path=[]
EVT_RESULT_ID = wx.NewId()
ID_START = wx.NewId()
ID_STOP = wx.NewId()

def EVT_RESULT(win, func):
    win.Connect(-1, -1, EVT_RESULT_ID, func)

class ResultEvent(wx.PyEvent):
    """Simple event to carry arbitrary result data."""
    def __init__(self, data):
        """Init Result Event."""
        wx.PyEvent.__init__(self)
        self.SetEventType(EVT_RESULT_ID)
        self.data = data
# Thread class that executes processing
class WorkerThread(Thread):
    """Worker Thread Class."""
    def __init__(self, notify_window):
        """Init Worker Thread Class."""
        Thread.__init__(self)
        self._notify_window = notify_window
        self._want_abort = 0
        # This starts the thread running on creation, but you could
        # also make the GUI thread responsible for calling this
        self.start()

    def run(self):
        """Run Worker Thread."""
        cutVideo(path)
        if self._want_abort:
            wx.PostEvent(self._notify_window, ResultEvent(None))
            return
        wx.PostEvent(self._notify_window, ResultEvent(True))

    def abort(self):
        """abort worker thread."""
        # Method for use by main thread to signal an abort
        self._want_abort = 1
########################################################################
class StoriesDragDrop(wx.FileDropTarget):
    """"""
    #----------------------------------------------------------------------
    def __init__(self, window):
        """Constructor"""
        wx.FileDropTarget.__init__(self)
        self.window = window

    #----------------------------------------------------------------------
    def OnDropFiles(self, x, y, filenames):
        global path
        self.window.SetInsertionPointEnd()
        for filepath in filenames:
            path.append(filepath)
            self.window.updateText(filepath + '\n')
            return True
########################################################################
class Panel(wx.Panel):
    """"""
    #----------------------------------------------------------------------
    def __init__(self, parent):
        """Constructor"""
        wx.Panel.__init__(self, parent=parent)

        file_drop_target = StoriesDragDrop(self)
        text = wx.StaticText(self, label="Drag and drop a video here: ")
        self.fileTextCtrl = wx.TextCtrl(self, style=wx.TE_MULTILINE|wx.TE_READONLY)
        self.fileTextCtrl.SetDropTarget(file_drop_target)
        sizer = wx.BoxSizer(wx.VERTICAL)
        sizer.Add(text, 0, wx.ALL, 5)
        sizer.Add(self.fileTextCtrl, 20, wx.EXPAND| wx.ALL, 20)
        self.button1 = wx.Button(self, ID_START, label='Start', size=(50, 30))
        self.button2 = wx.Button(self, ID_STOP, label='Cancel',  size=(50, 30))
        sizer.Add(self.button1, 2, wx.EXPAND, border=15)
        sizer.Add(self.button2, 2, wx.EXPAND, border=15)
        self.SetSizer(sizer)




    #----------------------------------------------------------------------
    def SetInsertionPointEnd(self):
        """
        Put insertion point at end of text control to prevent overwriting
        """
        self.fileTextCtrl.SetInsertionPointEnd()

    #----------------------------------------------------------------------
    def updateText(self, text):
        """
        Write text to the text control
        """
        self.fileTextCtrl.WriteText(text)


########################################################################
class Frame(wx.Frame):
    """"""
    #----------------------------------------------------------------------
    def __init__(self):
        """Constructor"""
        wx.Frame.__init__(self, parent=None, title="Stories Cutter")
        panel = Panel(self)
        self.Show()
        self.SetSize(520, 250)
        self.CreateStatusBar()
        self.worker = None

        self.Bind(wx.EVT_BUTTON, self.Begin, id=ID_START)
        self.Bind(wx.EVT_BUTTON, self.OnStop, id=ID_STOP)

        EVT_RESULT(self,self.OnResult)

    def Aguarde(self):
        wx.MessageBox('Isso pode demorar um pouco.', 'Aguarde um instante',
        wx.OK | wx.ICON_INFORMATION)

    def MessageSave(self):
        wx.MessageBox('Video(s) converted sucessfully!', '',
        wx.OK | wx.ICON_INFORMATION)

    def MessageErro(self):
        wx.MessageBox('No video was inserted.', 'Error',
        wx.OK | wx.ICON_INFORMATION)

    def Begin(self, event):
        if path == '' :
            self.MessageErro()
            event.Skip()
        else:
            if not self.worker:
                self.worker = WorkerThread(self)
                self.SetStatusText('Converting Video(s)..')

    def OnResult(self, event):
        """Show Result status."""
        if event.data is None:
            self.SetStatusText('Conversion aborted')
        else:
            self.SetStatusText('Finished conversion.')
            self.MessageSave()
        self.worker = None

    def OnStop(self, event):
        """Stop Computation."""
        if self.worker:
            self.SetStatusText('Aborting the conversion...')
            time.sleep(2)
            self.worker.abort()
#----------------------------------------------------------------------
if __name__ == "__main__":
    app = wx.App(False)
    frame = Frame()
    app.MainLoop()
