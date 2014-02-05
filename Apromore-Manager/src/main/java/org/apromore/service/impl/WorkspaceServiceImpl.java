package org.apromore.service.impl;

import org.apromore.dao.FolderRepository;
import org.apromore.dao.FolderUserRepository;
import org.apromore.dao.ProcessRepository;
import org.apromore.dao.ProcessUserRepository;
import org.apromore.dao.UserRepository;
import org.apromore.dao.WorkspaceRepository;
import org.apromore.dao.dataObject.FolderTreeNode;
import org.apromore.dao.model.Folder;
import org.apromore.dao.model.FolderUser;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.ProcessUser;
import org.apromore.dao.model.User;
import org.apromore.dao.model.Workspace;
import org.apromore.service.WorkspaceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of the SecurityService Contract.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class WorkspaceServiceImpl implements WorkspaceService {

    private WorkspaceRepository workspaceRepo;
    private ProcessRepository processRepo;
    private ProcessUserRepository processUserRepo;
    private FolderRepository folderRepo;
    private FolderUserRepository folderUserRepo;
    private UserRepository userRepo;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param workspaceRepository Workspace Repository.
     * @param userRepository User Repository.
     * @param processRepository Process Repository.
     * @param folderRepository Folder Repository.
     */
    @Inject
    public WorkspaceServiceImpl(final WorkspaceRepository workspaceRepository, final UserRepository userRepository,
            final ProcessRepository processRepository, final ProcessUserRepository processUserRepository,
            final FolderRepository folderRepository, final FolderUserRepository folderUserRepository) {
        workspaceRepo = workspaceRepository;
        userRepo = userRepository;
        processRepo = processRepository;
        processUserRepo = processUserRepository;
        folderRepo = folderRepository;
        folderUserRepo = folderUserRepository;
    }


    @Override
    public Folder getFolder(Integer folderId) {
        return folderRepo.findOne(folderId);
    }

    @Override
    public List<FolderUser> getFolderUsers(Integer folderId) {
        return folderUserRepo.findByFolder(folderRepo.findOne(folderId));
    }

    @Override
    public List<ProcessUser> getProcessUsers(Integer processId) {
        return processUserRepo.findByProcess(processRepo.findOne(processId));
    }

    @Override
    public List<ProcessUser> getUserProcessesOrig(String userId, Integer folderId) {
        if (folderId == 0) {
            return processUserRepo.findRootProcessesByUser(userId);
        } else {
            return processUserRepo.findAllProcessesInFolderForUser(folderId, userId);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void createFolder(String userId, String folderName, Integer parentFolderId) {
        Folder folder = new Folder();
        folder.setName(folderName);
        User user = userRepo.findByRowGuid(userId);

        if (parentFolderId != 0) {
            Folder parent = folderRepo.findOne(parentFolderId);
            if (parent != null) {
                folder.setParentFolder(parent);
            }
        }

        Workspace workspace = workspaceRepo.findOne(1);
        folder.setWorkspace(workspace);
        folder.setCreatedBy(user);
        folder.setModifiedBy(user);
        folder.setDateCreated(Calendar.getInstance().getTime());
        folder.setDateModified(Calendar.getInstance().getTime());
        folder.setDescription("");
        folder = folderRepo.save(folder);

        FolderUser fUser = new FolderUser();
        fUser.setFolder(folder);
        fUser.setUser(user);
        fUser.setHasOwnership(true);
        fUser.setHasWrite(true);
        fUser.setHasRead(true);

        folderUserRepo.save(fUser);
    }

    @Override
    public void updateFolder(Integer folderId, String folderName) {
        Folder folder = folderRepo.findOne(folderId);
        folder.setName(folderName);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteFolder(Integer folderId) {
        Folder folder = folderRepo.findOne(folderId);
        if (folder != null) {
            folderRepo.delete(folder);
        }
    }

    @Override
    public List<FolderTreeNode> getWorkspaceFolderTree(String userId) {
        return folderRepo.getFolderTreeByUser(0, userId);
    }

    @Override
    public List<Folder> getBreadcrumbs(Integer folderId) {
        List<Folder> folders = new ArrayList<>();

        Folder folder = folderRepo.findOne(folderId);
        if (folder != null) {
            folders.add(folder);
            while (folder.getParentFolder() != null && folder.getParentFolder().getId() != 0) {
                folder = folderRepo.findOne(folder.getParentFolder().getId());
                folders.add(folder);
            }
        }

        return folders;
    }

    @Override
    public List<FolderUser> getSubFolders(String userId, Integer folderId) {
        return folderUserRepo.findByParentFolderAndUser(folderId, userId);
    }

    @Override
    @Transactional(readOnly = false)
    public String saveFolderPermissions(Integer folderId, String userId, boolean hasRead, boolean hasWrite, boolean hasOwnership) {
        Folder folder = folderRepo.findOne(folderId);
        User user = userRepo.findByRowGuid(userId);

        createFolderUser(folder, user, hasRead, hasWrite, hasOwnership);

        Folder parentFolder = folder.getParentFolder();
        while (parentFolder != null && parentFolder.getId() > 0) {
            parentFolder = folderRepo.findOne(parentFolder.getId());
            createFolderUser(parentFolder, user, true, false, false);
            parentFolder = parentFolder.getParentFolder();
        }
        saveSubFolderPermissions(folder, user, hasRead, hasWrite, hasOwnership);

        return "";
    }

    @Override
    @Transactional(readOnly = false)
    public String removeFolderPermissions(Integer folderId, String userId) {
        Folder folder = folderRepo.findOne(folderId);
        User user = userRepo.findByRowGuid(userId);
        removeFolderUser(folder, user);
        removeSubFolderPermissions(folder, user);
        return "";
    }

    @Override
    @Transactional(readOnly = false)
    public String removeProcessPermissions(Integer processId, String userId) {
        Process process = processRepo.findOne(processId);
        User user = userRepo.findByRowGuid(userId);
        removeProcessUser(process, user);
        return "";
    }

    @Override
    @Transactional(readOnly = false)
    public String saveProcessPermissions(Integer processId, String userId, boolean hasRead, boolean hasWrite, boolean hasOwnership) {
        Process process = processRepo.findOne(processId);
        User user = userRepo.findByRowGuid(userId);

        createProcessUser(process, user, hasRead, hasWrite, hasOwnership);

        Folder parentFolder = process.getFolder();
        while (parentFolder != null && parentFolder.getId() > 0) {
            parentFolder = folderRepo.findOne(parentFolder.getId());
            createFolderUser(parentFolder, user, true, false, false);
            parentFolder = parentFolder.getParentFolder();
        }

        return "";
    }

    @Override
    @Transactional(readOnly = false)
    public void addProcessToFolder(Integer processId, Integer folderId) {
        if (folderId != null && processId != null) {
            Process process = processRepo.findOne(processId);
            Folder folder = folderRepo.findOne(folderId);

            process.setFolder(folder);
//            if (folder != null) {
//                folder.addFolderProcess(process);
//            }

            processRepo.save(process);
        }
    }

    /**
     * @see org.apromore.service.WorkspaceService#updatePublicFoldersForUsers(org.apromore.dao.model.Folder, java.util.List)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public void updatePublicFoldersForUsers(final Folder folder, final List<User> users) {
        if (folder != null) {
            // Update the Users for this folder.
            for (User user : users) {
                FolderUser folderUser = folderUserRepo.findByFolderAndUser(folder, user);
                if (folderUser == null) {
                    // We only want to create new permissions, we don't want to change existing permissions.
                    createFolderUser(folder, user, true, false, false);
                }
            }

            // Move up the tree one and start again. null = root folder that everyone has access.
            if (folder.getParentFolder() != null && folder.getParentFolder().getId() != null) {
                updatePublicFoldersForUsers(folder.getParentFolder(), users);
            }
        }
    }

    /**
     * @see org.apromore.service.WorkspaceService#createPublicStatusForUsers(org.apromore.dao.model.Process)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public void createPublicStatusForUsers(final Process process) {
        List<User> users = userRepo.findAll();

        for (User user : users) {
            if (!process.getUser().getId().equals(user.getId())) {
                createProcessUser(process, user, true, false, false);
            }
        }
    }

    /**
     * @see org.apromore.service.WorkspaceService#removePublicStatusForUsers(org.apromore.dao.model.Process)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public void removePublicStatusForUsers(final Process process) {
        Set<ProcessUser> freshProcessUserList = new HashSet<>();
        for (ProcessUser processUser : process.getProcessUsers()) {
            if (!processUser.getUser().getId().equals(process.getUser().getId())) {
                processUser.getUser().getProcessUsers().remove(processUser);
                processUserRepo.delete(processUser);
            } else {
                freshProcessUserList.add(processUser);
            }
        }
        process.setProcessUsers(freshProcessUserList);
    }

    /**
     * @see org.apromore.service.WorkspaceService#updateUsersPublicModels(org.apromore.dao.model.User)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public void updateUsersPublicModels(User user) {
        List<Process> processes = processRepo.findAll();
        for (Process process : processes) {
            createProcessUser(process, user, true, false, false);
            createFolderUser(process.getFolder(), user, true, false, false);
        }
    }




    /* Save the Sub Folder Permissions. */
    private void saveSubFolderPermissions(Folder folder, User user, boolean hasRead, boolean hasWrite, boolean hasOwnership) {
        for (Folder subFolder : folder.getSubFolders()) {
            createFolderUser(subFolder, user, hasRead, hasWrite, hasOwnership);
            saveSubFolderPermissions(subFolder, user, hasRead, hasWrite, hasOwnership);
        }
        for (Process process : folder.getProcesses()) {
            createProcessUser(process, user, hasRead, hasWrite, hasOwnership);
        }
    }

    /* Delete the sub Folder permissions. */
    private void removeSubFolderPermissions(Folder folder, User user) {
        for (Folder subFolder : folder.getSubFolders()) {
            removeFolderUser(subFolder, user);
            removeSubFolderPermissions(subFolder, user);
        }
        for (Process process : folder.getProcesses()) {
            removeProcessUser(process, user);
        }
    }

    private void createFolderUser(Folder folder, User user, boolean hasRead, boolean hasWrite, boolean hasOwnership) {
        FolderUser folderUser = folderUserRepo.findByFolderAndUser(folder, user);
        if (folderUser == null) {
            folderUser = new FolderUser();
            folderUser.setFolder(folder);
            folderUser.setUser(user);
            folderUser.setHasRead(hasRead);
            folderUser.setHasWrite(hasWrite);
            folderUser.setHasOwnership(hasOwnership);
        } else {
            folderUser.setHasRead(hasRead);
            folderUser.setHasWrite(hasWrite);
            folderUser.setHasOwnership(hasOwnership);
        }

        folderUserRepo.save(folderUser);
    }

    private void createProcessUser(Process process, User user, boolean hasRead, boolean hasWrite, boolean hasOwnership) {
        ProcessUser processUser = processUserRepo.findByProcessAndUser(process, user);
        if (processUser == null) {
            processUser = new ProcessUser();
            processUser.setProcess(process);
            processUser.setUser(user);
            processUser.setHasRead(hasRead);
            processUser.setHasWrite(hasWrite);
            processUser.setHasOwnership(hasOwnership);

            process.getProcessUsers().add(processUser);
            user.getProcessUsers().add(processUser);
        } else {
            processUser.setHasRead(hasRead);
            processUser.setHasWrite(hasWrite);
            processUser.setHasOwnership(hasOwnership);
        }

        processUserRepo.save(processUser);
    }

    private void removeFolderUser(Folder folder, User user) {
        FolderUser folderUser = folderUserRepo.findByFolderAndUser(folder, user);
        if (folderUser != null) {
            folderUserRepo.delete(folderUser);
        }
    }

    private void removeProcessUser(Process process, User user) {
        ProcessUser processUser = processUserRepo.findByProcessAndUser(process, user);
        if (processUser != null) {
            processUserRepo.delete(processUser);
        }
    }

}
