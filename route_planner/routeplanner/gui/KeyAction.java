package routeplanner;

import java.awt.event.ActionEvent; 
import javax.swing.AbstractAction;
import javax.swing.JButton;

public class KeyAction extends AbstractAction
{
  private JButton ok;
   
  public KeyAction(JButton button)
  {
    this.ok = button;
  }
  public void actionPerformed(ActionEvent e)
  {
    ok.doClick();
  }
}
