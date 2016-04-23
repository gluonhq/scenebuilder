package com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven;

import com.oracle.javafx.scenebuilder.app.AppPlatform;
import com.oracle.javafx.scenebuilder.app.i18n.I18N;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.codehaus.plexus.util.FileUtils;
import org.eclipse.aether.AbstractRepositoryListener;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.installation.InstallRequest;
import org.eclipse.aether.installation.InstallationException;
import org.eclipse.aether.metadata.DefaultMetadata;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
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
import org.eclipse.aether.version.Version;

public class MavenRepositorySystem {

    // TODO: Manage List of Repositories
    private final List<Repository> repositories = Arrays.asList(
            new Repository("Maven Central", "default", "https://repo1.maven.org/maven2/"),
            new Repository("Jcenter", "default", "https://jcenter.bintray.com"),
            new Repository("Sonatype (snapshots)", "default", "https://oss.sonatype.org/content/repositories/snapshots"),
            new Repository("Sonatype (releases)", "default", "https://oss.sonatype.org/content/repositories/releases"),
            new Repository("Gluon Nexus", "default", "http://nexus.gluonhq.com/nexus/content/repositories/releases"));
    
    // TODO: Manage private repositories and credentials
    
    private RepositorySystem system;
    
    private DefaultRepositorySystemSession session;
    
    private LocalRepository localRepo;
    
    private VersionRangeResult rangeResult;
    
    public MavenRepositorySystem() {
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
                throw new RuntimeException(I18N.getString("maven.dialog.creation.failed"), exception);
            }
        });
        
        system = locator.getService(RepositorySystem.class);
        
        session = MavenRepositorySystemUtils.newSession();

        localRepo = new LocalRepository(new File(AppPlatform.getUserM2Repository()));
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
        return repositories.stream()
                .map(this::createRepository)
                .collect(Collectors.toList());
    }
    
    public RemoteRepository getRemoteRepository(Version version) {
        if (rangeResult == null) {
            return null;
        }
        return getRepositories()
                .stream()
                .filter(r -> r.getId().equals(rangeResult.getRepository(version).getId()))
                .findFirst()
                .orElse(new RemoteRepository
                        .Builder("Local", "default", session.getLocalRepository().getBasedir().getAbsolutePath())
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
    
    private void cleanMetadata(Artifact artifact) {
        final String path = localRepo.getBasedir().getAbsolutePath() + File.separator
                        + artifact.getGroupId().replaceAll("\\.", File.separator) + File.separator
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
        
        LocalRepository localTmpRepo = new LocalRepository(AppPlatform.getTempM2Repository());
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localTmpRepo));
        
        List<Artifact> artifacts = Stream.of(artifact)
                .map(a -> {
                    ArtifactRequest artifactRequest = new ArtifactRequest();
                    artifactRequest.setArtifact(a);
                    artifactRequest.setRepositories(Arrays.asList(remoteRepository));
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
            FileUtils.deleteDirectory(AppPlatform.getTempM2Repository());
        } catch (IOException ex) { }
        
        return absolutePath;
    }
        
    public String resolveDependencies(RemoteRepository remoteRepository, Artifact artifact) {
        DependencyFilter classpathFlter = DependencyFilterUtils.classpathFilter(JavaScopes.COMPILE);
        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot(new Dependency(artifact, JavaScopes.COMPILE));
        collectRequest.setRepositories(Arrays.asList(remoteRepository));

        DependencyRequest dependencyRequest = new DependencyRequest(collectRequest, classpathFlter);
        try {
            List<ArtifactResult> artifactResults = system.resolveDependencies(session, dependencyRequest)
                    .getArtifactResults();
            
            return artifactResults.stream()
                    .skip(1) // exclude jar itself
                    .map(a -> a.getArtifact().getFile().getAbsolutePath())
                    .collect(Collectors.joining(":"));
        } catch (DependencyResolutionException ex) { }
        return "";
    }
    
    private RemoteRepository createRepository(Repository repository) {
        return new RemoteRepository
                .Builder(repository.getId() , repository.getType(), repository.getURL())
                .build();
    }
    
}
