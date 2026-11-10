# Lab 24: Initial Ansible Configuration and Ad-Hoc Execution

## Objective
- Install and configure Ansible on the control node.
- Generate a new SSH key on the control node.
- Transfer the public key to the managed node using `ssh-copy-id`.
- Create an inventory for the managed node.
- Run an ad-hoc command to install Docker.

## Environment
| Role | Host | OS | IP |
|------|------|----|----|
| Control node | RHEL VM | RHEL 10 | 192.168.198.134 |
| Managed node | Same RHEL VM (accessed over SSH by IP) | RHEL 10 | 192.168.198.134 |

The control node and the managed node are the same machine, and Ansible connects to it over SSH using its IP address, as it would to a remote host.

## Step 1: Install Ansible on the control node
The RHEL VM is not registered with Red Hat, so `dnf install ansible-core` could not find the package. Ansible was already installed for the user with pip.

```bash
ansible --version
```
![ansible version](screenshots/01-ansible-version.png)

Result: `ansible [core 2.21.4]` with Python 3.12.

## Step 2: Generate a new SSH key
A key named `id_ed25519` already existed, so a new dedicated key was generated instead of overwriting it.

```bash
ssh-keygen -t ed25519 -f ~/.ssh/ansible_key -N "" -C "ansible-control"
ls -l ~/.ssh/ansible_key*
```
![ssh keygen](screenshots/02-ssh-keygen.png)

## Step 3: Copy the public key to the managed node
```bash
ssh-copy-id -i ~/.ssh/ansible_key.pub sofiabahaa@192.168.198.134
```
![ssh-copy-id](screenshots/03-ssh-copy-id.png)

Verify passwordless login:
```bash
ssh -i ~/.ssh/ansible_key sofiabahaa@192.168.198.134 "hostname && whoami"
```
![ssh test](screenshots/04-ssh-test.png)

## Step 4: Create the inventory and configuration
```bash
mkdir -p ~/ansible-labs && cd ~/ansible-labs
```

`inventory`:
```ini
[managed]
node1 ansible_host=192.168.198.134 ansible_user=sofiabahaa ansible_ssh_private_key_file=~/.ssh/ansible_key
```

`ansible.cfg`:
```ini
[defaults]
inventory = ./inventory
host_key_checking = False
interpreter_python = auto_silent

[privilege_escalation]
become = False
```
![inventory](screenshots/05-inventory.png)

Test connectivity:
```bash
ansible managed -m ping
```
![ping](screenshots/06-ping.png)

Result: `node1 | SUCCESS`, `"ping": "pong"`.

## Step 5: Ad-hoc command to install Docker
```bash
ansible managed -b -K -m ansible.builtin.dnf -a "name=docker-ce state=present"
```
![ad-hoc docker](screenshots/07-adhoc-docker.png)

Result: `SUCCESS` with `"changed": false` and `"msg": "Nothing to do"`.
Docker (`docker-ce-29.8.1`) was already installed from the `docker-ce-stable` repo, so Ansible made no change. This shows the idempotent behavior of the module.

## Notes
- `-b` runs the task with privilege escalation (sudo) and `-K` asks for the sudo password.
- `host_key_checking = False` is used for the lab only.
