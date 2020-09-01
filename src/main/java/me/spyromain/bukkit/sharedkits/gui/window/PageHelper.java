package me.spyromain.bukkit.sharedkits.gui.window;

import java.util.List;

public class PageHelper<T> {
    public static final int FIRST_PAGE = 0;

    private final List<T> list;
    private final int maxPageSize;
    private final int lastPage;

    public PageHelper(List<T> list, int maxPageSize) {
        this.list = list;
        this.maxPageSize = maxPageSize;
        lastPage = Math.max(FIRST_PAGE, list.size() - 1) / maxPageSize;
    }

    public List<T> getList() {
        return list;
    }

    public int getMaxPageSize() {
        return maxPageSize;
    }

    public List<T> getListPage(int page) {
        if (page < FIRST_PAGE || lastPage < page) {
            throw new IllegalArgumentException();
        }

        int fromIndex = page * maxPageSize;
        int toIndex = Math.min(list.size(), fromIndex + maxPageSize);
        return list.subList(fromIndex, toIndex);
    }

    public int getNearestPage(int page) {
        return Math.max(FIRST_PAGE, Math.min(page, lastPage));
    }

    public boolean isFirstPage(int page) {
        return page == FIRST_PAGE;
    }

    public boolean isLastPage(int page) {
        return page == lastPage;
    }
}
