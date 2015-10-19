package ExplodetoWin;
import GamePackage.Board;
import java.awt.EventQueue;
import javax.swing.JFrame;
import ClientServerComm.EtWClient;

public class ExplodeToWin extends JFrame {

	private static final long serialVersionUID = 1L;
	public StartMenu menu;
        public Board board;
        public LoadingUI loadingUI;
        public static String defaultServer;
        public static String defaultName;
        
	public ExplodeToWin () {
		initUI ();
	}
	
	private void initUI() {

        //add(new Board());
        //add(new UI());
		
        menu = new StartMenu(this,defaultServer, defaultName);
        add(menu);
        setResizable(false);
        pack();
        setSize(1206, 728);

        setTitle("EtW");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }    
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
            if (args.length > 1){
                defaultServer = args[0];

            }
            if (args.length == 2){
                defaultName = args[1];
            }
		EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ExplodeToWin ex = new ExplodeToWin();
                ex.setVisible(true);
            }
        });
	}
	
        public void addLoadingUI(String port, String server, String playerName){
            remove (menu);
            loadingUI = new LoadingUI(this, port, server, playerName);
            add(loadingUI);
            loadingUI.initClient();
        }
	public void addBoard(EtWClient client, Map map, Long startDate, int maxConnections, String playerName)
	{
            remove (loadingUI);
            board = new Board(startDate, maxConnections, client, map, this, playerName);
            client.setBoard(board);
            add(board);
            System.gc();
            

        //add(new UI());
	}
        
	
	public void addMapMaker()
	{
            remove(menu);
            add(new MapMaker());
        //add(new UI());
	}
	
	

}
