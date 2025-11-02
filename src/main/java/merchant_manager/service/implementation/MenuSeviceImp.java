package merchant_manager.service.implementation;

import merchant_manager.models.Menu;
import merchant_manager.repository.MenuRepository;
import merchant_manager.service.MenuService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuSeviceImp implements MenuService {

    private final MenuRepository menuRepository;

    public MenuSeviceImp(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    @Override
    public List<Menu> getMenus() {
        return menuRepository.findAll();
    }

}
