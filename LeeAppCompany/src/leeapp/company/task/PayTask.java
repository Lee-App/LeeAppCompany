package leeapp.company.task;

import cn.nukkit.Player;
import cn.nukkit.scheduler.PluginTask;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import leeapp.company.Company;
import leeapp.company.LeeAppCompany;
import me.onebone.economyapi.EconomyAPI;

public class PayTask extends PluginTask<LeeAppCompany> {
	
	private static String BASE = "[ LA_Company ] ";
	private Config company;
	private Company com = new Company();
	
	public PayTask(LeeAppCompany owner, Config company) {
		super(owner);
		this.company = company;
	}

	@Override
	public void onRun(int arg0) {
		getOwner().getServer().broadcastMessage(TextFormat.DARK_BLUE + BASE + TextFormat.DARK_PURPLE + "�޿� ���޽ð��� �Ǿ� �� ȸ�縶�� ������ �ݾ��� �޿��� ���޵˴ϴ�.");
		for(String name : com.getCompanyList(company)){
			if((com.getOnlineList(getOwner().getServer(), name, company) * com.getAmount(name, company)) > com.getFund(name, company)){
				for(String user : com.getList(name, company)){
					Player player = getOwner().getServer().getPlayer(user);
					if(player != null && !com.isOwner(player, company)){
						com.reduceFund(com.getAmount(name, company), name, company);
						EconomyAPI.getInstance().addMoney(player, com.getAmount(name, company));
						player.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "ȸ�翡�� �޿����޽ð��� �Ǿ� �޿��� �޾ҽ��ϴ�. ( " + com.getAmount(name, company) + "�� )");
					}
				}
			}else{
				getOwner().getServer().broadcastMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + name + "ȸ��� �ڱ��� �����Ͽ� �޿��� ���޵��� �ʽ��ϴ�!");
			}
		}
	}


}
