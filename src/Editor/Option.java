package Editor;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class Option<K extends Component> extends JPanel {
	private K	component;

	public K getRightComponent() {
		return component;
	}

	public Option(String _label, K _component) {
		component = _component;

		super.setLayout(new BorderLayout());
		super.add(new JLabel(_label), BorderLayout.WEST);
		super.add(_component, BorderLayout.CENTER);
	}
}

