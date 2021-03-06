package com.github.dockerjava.client.command;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dockerjava.client.DockerException;
import com.google.common.base.Preconditions;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static javax.ws.rs.client.Entity.entity;

/**
 * Pause a container.
 *
 * @param containerId - Id of the container
 *
 */
public class PauseContainerCmd extends AbstrDockerCmd<PauseContainerCmd, Integer> {

	private static final Logger LOGGER = LoggerFactory.getLogger(PauseContainerCmd.class);

	private String containerId;

	public PauseContainerCmd(String containerId) {
		withContainerId(containerId);
	}

    public String getContainerId() {
        return containerId;
    }

    public PauseContainerCmd withContainerId(String containerId) {
		Preconditions.checkNotNull(containerId, "containerId was not specified");
		this.containerId = containerId;
		return this;
	}

	@Override
    public String toString() {
        return new StringBuilder("pause ")
            .append(containerId)
            .toString();
    }

	protected Integer impl() throws DockerException {
		WebTarget webResource = baseResource.path("/containers/{id}/pause").resolveTemplate("id", containerId);

		Response response = null;

		try {
			LOGGER.trace("POST: {}", webResource);
			response = webResource.request().accept(MediaType.APPLICATION_JSON).post(entity(null, MediaType.APPLICATION_JSON), Response.class);
		} catch (ClientErrorException exception) {
			if (exception.getResponse().getStatus() == 404) {
				LOGGER.warn("No such container {}", containerId);
			} else if (exception.getResponse().getStatus() == 204) {
				//no error
				LOGGER.trace("Successfully paused container {}", containerId);
			} else if (exception.getResponse().getStatus() == 500) {
				throw new DockerException("Server error", exception);
			} else {
				throw new DockerException(exception);
			}
		}

		return response.getStatus();
	}
}
