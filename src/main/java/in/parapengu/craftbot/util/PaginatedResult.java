package in.parapengu.craftbot.util;

import in.parapengu.craftbot.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public class PaginatedResult {

	private String header;
	private List<String> rows;
	private int results;
	private boolean number;

	public PaginatedResult(String header, List<String> rows, int results, boolean number) {
		this.header = header;
		this.rows = rows;
		this.results = results;
		this.number = number;
	}

	public int getPages() {
		if(getRows().size() == 0) {
			return 0;
		}

		if(getRows().size() % results == 0) {
			return getRows().size() / results;
		}

		return ((getRows().size() - (getRows().size() % results)) / results) + 1;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getHeader(int page) {
		return header.replace("[page]", page + "").replace("[pages]", getPages() + "");
	}

	public List<String> getRows() {
		return rows;
	}

	public List<String> getRows(int page) {
		List<String> rows = new ArrayList<>();
		/*
			0-[results]
			[results * (page - 1)] -> [results * page]
		 */
		if(getRows().size() <= results) {
			rows.addAll(getRows());
		} else {
			int start = results * (page - 1);
			if(page == 1) {
				start = 0;
			}

			int finish = results * page;
			if(finish >= getRows().size()) {
				finish = getRows().size() - 1;
			}

			rows = getRows().subList(start, finish);
		}

		return rows;
	}

	public void display(Logger logger) {
		display(logger, 1);
	}

	public void display(Logger logger, int page) {
		if(page > getPages() || page < 1) {
			logger.warning("That page does not exist");
			return;
		}

		List<String> rows = getRows(page);

		logger.info(getHeader(page));

		int i = 1 + ((page - 1) * results);
		for(String row : rows) {
			if(number) {
				row = i + ". " + row;
			}

			logger.info(row);
			i++;
		}
	}

}
