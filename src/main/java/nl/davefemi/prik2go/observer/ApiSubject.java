package nl.davefemi.prik2go.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ounl
 */
public abstract class ApiSubject {

	private List<ApiObserver> apiObservers = new ArrayList<>();
	
	public void attach(ApiObserver apiObserver) {
		if (!apiObservers.contains(apiObserver)) {
			apiObservers.add(apiObserver);
		}
	}
	
	public void detach(ApiObserver apiObserver) {
		int index = apiObservers.indexOf(apiObserver);
		if (index != -1) {
			apiObservers.remove(index);
		}
	}

	public void notifyObservers() {
	  for (ApiObserver o: apiObservers) {
	    o.update(this, null);
	  };
	}
			
	public void notifyObservers(Object arg) {
	  for (ApiObserver o: apiObservers) {
	    o.update(this, arg);
	  };
	}

}
