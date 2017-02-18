/*
 * Copyright (c) 2016, Gluon and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation and Gluon nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven;

import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.repository.Repository;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.preset.MavenPresets;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.oracle.javafx.scenebuilder.kit.preferences.RepositoryPreferences;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.codehaus.plexus.util.FileUtils;
import org.eclipse.aether.AbstractRepositoryListener;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.installation.InstallRequest;
import org.eclipse.aether.installation.InstallationException;
import org.eclipse.aether.metadata.DefaultMetadata;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transfer.AbstractTransferListener;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.eclipse.aether.util.filter.DependencyFilterUtils;
import org.eclipse.aether.util.repository.AuthenticationBuilder;
import org.eclipse.aether.version.Version;

public class MavenRepositorySystem {

    // TODO: Manage List of Repositories
    // TODO: Manage private repositories and credentials
    
    private RepositorySystem system;
    
    private DefaultRepositorySystemSession session;
    
    private LocalRepository localRepo;
    
    private VersionRangeResult rangeResult;
    
    private final boolean onlyReleases;
    
    private BasicRepositoryConnectorFactory basicRepositoryConnectorFactory;

    private final String userM2Repository;
    private final String tempM2Repository;
    private final RepositoryPreferences repositoryPreferences;
    
    public MavenRepositorySystem(boolean onlyReleases, String userM2Repository, String tempM2Repository,
                                 RepositoryPreferences repositoryPreferences) {
        this.onlyReleases = onlyReleases;
        this.userM2Repository = userM2Repository;
        this.tempM2Repository = tempM2Repository;
        this.repositoryPreferences = repositoryPreferences;
        initRepositorySystem();
    }
    
    private void initRepositorySystem() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
        locator.setErrorHandler(new DefaultServiceLocator.ErrorHandler() {
            @Override
            public void serviceCreationFailed(Class<?> type, Class<?> impl, Throwable exception) {
                throw new RuntimeException(exception);
            }
        });
        
        basicRepositoryConnectorFactory = new BasicRepositoryConnectorFactory();
        basicRepositoryConnectorFactory.initService(locator);
        
        system = locator.getService(RepositorySystem.class);

        session = MavenRepositorySystemUtils.newSession();

        localRepo = new LocalRepository(new File(userM2Repository));
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));

        // TODO: log file transfers
        session.setTransferListener(new AbstractTransferListener() {
            @Override
            public void transferSucceeded(TransferEvent event) { }

            @Override
            public void transferFailed(TransferEvent event) { }
        });
        
        // TODO: Log repository changes
        session.setRepositoryListener(new AbstractRepositoryListener() {
            @Override
            public void artifactResolved(RepositoryEvent event) { }
        });
        
    }

    public RepositorySystem getRepositorySystem() {
        return system;
    }
    
    public DefaultRepositorySystemSession getRepositorySession() {
        return session;
    }
    
    public List<RemoteRepository> getRepositories() {
        final List<RemoteRepository> list = MavenPresets.getPresetRepositories().stream()
                .filter(r -> !onlyReleases ||
                        (onlyReleases && !r.getId().toUpperCase(Locale.ROOT).contains("SNAPSHOT")))
                .map(this::createRepository)
                .collect(Collectors.toList());
        list.addAll(repositoryPreferences.getRepositories().stream()
                .filter(r -> !onlyReleases ||
                        (onlyReleases && !r.getId().toUpperCase(Locale.ROOT).contains("SNAPSHOT")))
                .map(this::createRepository)
                .collect(Collectors.toList()));
        return list;
    }
    
    public RemoteRepository getRemoteRepository(Version version) {
        if (rangeResult == null || version == null) {
            return null;
        }
        return getRepositories()
                .stream()
                .filter(r -> r.getId().equals(rangeResult.getRepository(version).getId()))
                .findFirst()
                .orElse(new RemoteRepository
                        .Builder(MavenPresets.LOCAL, "default", session.getLocalRepository().getBasedir().getAbsolutePath())
                        .build());
    }
    
    public RemoteRepository getRemoteRepository(String name) {
        return getRepositories()
                .stream()
                .filter(r -> r.getId().equals(name))
                .findFirst()
                .orElse(new RemoteRepository
                        .Builder(MavenPresets.LOCAL, "default", session.getLocalRepository().getBasedir().getAbsolutePath())
                        .build());
    }
    
    public List<Version> findVersions(Artifact artifact) {
        VersionRangeRequest rangeRequest = new VersionRangeRequest();
        rangeRequest.setArtifact(artifact);
        rangeRequest.setRepositories(getRepositories());
        try {
            rangeResult = system.resolveVersionRange(session, rangeRequest);
            cleanMetadata(artifact);
            
            return rangeResult.getVersions();
        } catch (VersionRangeResolutionException ex) { } 
        return new ArrayList<>();
    }
    
    public Version findLatestVersion(Artifact artifact) {
        VersionRangeRequest rangeRequest = new VersionRangeRequest();
        rangeRequest.setArtifact(artifact);
        rangeRequest.setRepositories(getRepositories());
        try {
            rangeResult = system.resolveVersionRange(session, rangeRequest);
            cleanMetadata(artifact);
            return rangeResult.getVersions()
                    .stream()
                    .filter(v -> !v.toString().toLowerCase(Locale.ROOT).contains("snapshot"))
                    .sorted((v1, v2) -> v2.compareTo(v1))
                    .findFirst()
                    .orElse(null);
        } catch (VersionRangeResolutionException ex) { } 
        return null;
    }
    
    private void cleanMetadata(Artifact artifact) {
        final String path = localRepo.getBasedir().getAbsolutePath() + File.separator
                        + artifact.getGroupId().replaceAll("\\.", Matcher.quoteReplacement(File.separator)) + File.separator
                        + artifact.getArtifactId() + File.separator;
        final DefaultMetadata metadata = new DefaultMetadata("maven-metadata.xml", Metadata.Nature.RELEASE);
        getRepositories()
            .stream()
            .map(r -> session.getLocalRepositoryManager().getPathForRemoteMetadata(metadata, r, ""))
            .forEach(s -> {
                File file = new File(path + s);
                if (file.exists()) {
                    try {
                        Files.delete(file.toPath());
                        Files.delete(new File(path + s + ".sha1").toPath());
                    } catch (IOException ex) { }
                }
            });
    }
        
    public String resolveArtifacts(RemoteRepository remoteRepository, Artifact... artifact) {
        
        LocalRepository localTmpRepo = new LocalRepository(tempM2Repository);
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localTmpRepo));
        
        List<Artifact> artifacts = Stream.of(artifact)
                .map(a -> {
                    ArtifactRequest artifactRequest = new ArtifactRequest();
                    artifactRequest.setArtifact(a);
                    artifactRequest.setRepositories(remoteRepository == null ? getRepositories() : 
                            Arrays.asList(remoteRepository));
                    return artifactRequest;
                })
                .map(ar -> {
                    try {
                        ArtifactResult result = system.resolveArtifact(session, ar);
                        return result.getArtifact();
                    } catch (ArtifactResolutionException ex) { }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()); 
        
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));
        
        List<File> sha1Files = null;
        if (artifacts != null && !artifacts.isEmpty()) {
            sha1Files = artifacts.stream()
                .filter(a -> a.getFile() != null)
                .map(a -> new File(a.getFile().getAbsolutePath().concat(".sha1")))
                .collect(Collectors.toList());
        
            InstallRequest installRequest = new InstallRequest();
            installRequest.setArtifacts(artifacts);
            try {
                system.install(session, installRequest);
            } catch (InstallationException ex) { }
        } 

        // return path from local m2
        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact(artifact[0]);
        String absolutePath = "";
        try {
            final File jarFile = system.resolveArtifact(session, artifactRequest).getArtifact().getFile();
            absolutePath = jarFile.getAbsolutePath();
            if (sha1Files != null) {
                sha1Files.stream()
                    .forEach(f -> {
                        try {
                            FileUtils.copyFile(f, new File(jarFile.getParent() + File.separator + f.getName()));
                        } catch (IOException ex) { }
                    });
            }
        } catch (ArtifactResolutionException ex) { }
        
        try {
            FileUtils.deleteDirectory(tempM2Repository);
        } catch (IOException ex) { }
        
        return absolutePath;
    }
        
    public String resolveDependencies(RemoteRepository remoteRepository, Artifact artifact) {
        DependencyFilter classpathFlter = DependencyFilterUtils.classpathFilter(JavaScopes.COMPILE);
        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot(new Dependency(artifact, JavaScopes.COMPILE));
        collectRequest.setRepositories(remoteRepository == null ? getRepositories() : 
                Arrays.asList(remoteRepository));

        DependencyRequest dependencyRequest = new DependencyRequest(collectRequest, classpathFlter);
        try {
            List<ArtifactResult> artifactResults = system.resolveDependencies(session, dependencyRequest)
                    .getArtifactResults();
            
            return artifactResults.stream()
                    .skip(1) // exclude jar itself
                    .map(a -> a.getArtifact().getFile().getAbsolutePath())
                    .collect(Collectors.joining(File.pathSeparator));
        } catch (DependencyResolutionException ex) { }
        return "";
    }
    
    private RemoteRepository createRepository(Repository repository) {
        Authentication auth = null;
        if (repository.getUser() != null && !repository.getUser().isEmpty() && 
            repository.getPassword() != null && !repository.getPassword().isEmpty()) {
            auth = new AuthenticationBuilder()
                    .addUsername(repository.getUser())
                    .addPassword(repository.getPassword())
                    .build();
        }
        
        final RemoteRepository repo = new RemoteRepository
                .Builder(repository.getId() , repository.getType(), repository.getURL())
                .setSnapshotPolicy(onlyReleases ? new RepositoryPolicy(false, null, null) : new RepositoryPolicy())
                .setAuthentication(auth)
                .build();
        return repo;
    }
    
    public String validateRepository(Repository repository) {
        RemoteRepository remoteRepository = createRepository(repository);
        
        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact(new DefaultArtifact("test:test:1.0"));
        artifactRequest.setRepositories(Arrays.asList(remoteRepository));
        try {
            system.resolveArtifact(session, artifactRequest);
        } catch (ArtifactResolutionException ex) {
            final String rootCauseMessage = ExceptionUtils.getRootCauseMessage(ex);
            if (rootCauseMessage != null && !rootCauseMessage.contains("ArtifactNotFoundException")) {
                return rootCauseMessage;
            }
        }
        
        return "";
    }
}
