package no.goodtech.dashboard.model;

import java.util.List;

public class SeriesInfo {

	private final boolean fullFetch;
	private final List<SampleDTO> sampleDTOs;


	/**
	 * Bean holding information about the fetched series
	 *
	 * @param fullFetch True if this was a full fetch (i.e. not incremental)
	 * @param sampleDTOs The sample points
	 */
	public SeriesInfo(boolean fullFetch, List<SampleDTO> sampleDTOs) {
		this.fullFetch = fullFetch;
		this.sampleDTOs = sampleDTOs;
	}

	public boolean isFullFetch() {
		return fullFetch;
	}

	public List<SampleDTO> getSampleDTOs() {
		return sampleDTOs;
	}

	public void add(SampleDTO sampleDTO) {
		sampleDTOs.add(sampleDTO);
	}

	public void addAll(List<SampleDTO> sampleDTO) {
		sampleDTOs.addAll(sampleDTO);
	}

	public int count() {
		if (sampleDTOs != null) {
			return sampleDTOs.size();
		}
		return 0;
	}
}
