Imports System.IO

Public Class sudokuFiller

    Private Sub btnClear_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles btnClear.Click
        For i = 0 To 8 Step 1
            For j = 0 To 8 Step 1
                Controls.Find("txt" & i & j, True)(0).Text = ""
            Next
        Next
    End Sub

    Private Sub btnSave_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles btnSave.Click
        Dim fileName As String

        save.ShowDialog()
        fileName = save.FileName
        If fileName = "" Then
            MsgBox("File invalid.")
            Return
        Else
            Dim writer As StreamWriter = New StreamWriter(fileName)
            For i = 0 To 8 Step 1
                If i <> 3 And i <> 6 Then
                    writer.WriteLine(" --- --- ---  --- --- ---  --- --- ---")
                Else
                    writer.WriteLine("=======================================")
                End If
                For j = 0 To 8 Step 1
                    If j <> 3 And j <> 6 Then
                        writer.Write("| ")
                    Else
                        writer.Write("|| ")
                    End If
                    Dim num As Integer
                    Try
                        num = Integer.Parse(Controls.Find("txt" & i & j, True)(0).Text)
                    Catch ex As Exception
                        num = 0
                    End Try

                    If num < 1 Or num > 9 Then
                        writer.Write("0 ")
                    Else
                        writer.Write(num & " ")
                    End If
                    If j = 8 Then
                        writer.WriteLine("|")
                    End If
                Next
                If i = 8 Then
                    writer.WriteLine(" --- --- ---  --- --- ---  --- --- ---")
                End If
            Next
            writer.Close()
            MsgBox("File written successfully")
        End If
    End Sub

    Private Sub Load_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles Load.Click

    End Sub
End Class
